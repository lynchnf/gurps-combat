package norman.gurps.combat.service;

import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.ActionType;
import norman.gurps.combat.model.Battle;
import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.DefenseType;
import norman.gurps.combat.model.HealthStatus;
import norman.gurps.combat.model.NextStep;
import norman.gurps.combat.service.combat.CombatActionComponent;
import norman.gurps.combat.service.combat.CombatAimTargetComponent;
import norman.gurps.combat.service.combat.CombatBeginTurnComponent;
import norman.gurps.combat.service.combat.CombatDeathCheckComponent;
import norman.gurps.combat.service.combat.CombatDefenseComponent;
import norman.gurps.combat.service.combat.CombatEndTurnComponent;
import norman.gurps.combat.service.combat.CombatForDamageComponent;
import norman.gurps.combat.service.combat.CombatMeleeTargetComponent;
import norman.gurps.combat.service.combat.CombatRangedTargetComponent;
import norman.gurps.combat.service.combat.CombatToDefendComponent;
import norman.gurps.combat.service.combat.CombatToHitComponent;
import norman.gurps.combat.service.combat.CombatUnconsciousnessCheckComponent;
import norman.gurps.combat.service.combat.CombatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

@Service
public class CombatService {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatService.class);
    private BattleService battleService;
    private CombatBeginTurnComponent beginTurn;
    private CombatUnconsciousnessCheckComponent unconsciousnessCheck;
    private CombatActionComponent action;
    private CombatMeleeTargetComponent meleeTarget;
    private CombatAimTargetComponent aimTarget;
    private CombatRangedTargetComponent rangedTarget;
    private CombatToHitComponent toHit;
    private CombatDefenseComponent defense;
    private CombatToDefendComponent toDefend;
    private CombatForDamageComponent forDamage;
    private CombatDeathCheckComponent deathCheck;
    private CombatEndTurnComponent endTurn;
    private CombatUtils utils;

    public CombatService(BattleService battleService, CombatBeginTurnComponent beginTurn,
            CombatUnconsciousnessCheckComponent unconsciousnessCheck, CombatActionComponent action,
            CombatMeleeTargetComponent meleeTarget, CombatAimTargetComponent aimTarget,
            CombatRangedTargetComponent rangedTarget, CombatToHitComponent toHit, CombatDefenseComponent defense,
            CombatToDefendComponent toDefend, CombatForDamageComponent forDamage, CombatDeathCheckComponent deathCheck,
            CombatEndTurnComponent endTurn, CombatUtils utils) {
        this.battleService = battleService;
        this.beginTurn = beginTurn;
        this.unconsciousnessCheck = unconsciousnessCheck;
        this.action = action;
        this.meleeTarget = meleeTarget;
        this.aimTarget = aimTarget;
        this.rangedTarget = rangedTarget;
        this.toHit = toHit;
        this.defense = defense;
        this.toDefend = toDefend;
        this.forDamage = forDamage;
        this.deathCheck = deathCheck;
        this.endTurn = endTurn;
        this.utils = utils;
    }

    public void startCombat() {
        LOGGER.debug("Starting combat.");
        Battle battle = battleService.getBattle();

        if (battle.getNextStep() != null) {
            throw new LoggingException(LOGGER, "Battle has already started.");
        }
        if (battle.getCombatants().isEmpty()) {
            throw new LoggingException(LOGGER, "Battle has no combatants.");
        }

        // Sort combatants.
        Function<Combatant, Double> bs = combatant -> combatant.getGameChar().getBasicSpeed();
        Function<Combatant, Integer> dx = combatant -> combatant.getGameChar().getDexterity();
        Comparator<Combatant> comp = Comparator.comparing(bs).thenComparing(dx).reversed();
        battle.getCombatants().sort(comp);

        // Initialize all combatants.
        for (Combatant combatant : battle.getCombatants()) {
            combatant.getReadyItems().addAll(combatant.getGameChar().getDefaultReadyItems());
            combatant.setCurrentDamage(0);
            combatant.setPreviousDamage(0);
            combatant.setUnconsciousnessCheckFailed(false);
            combatant.setNbrOfDeathChecksNeeded(0);
            combatant.setDeathCheckFailed(false);
            HealthStatus healthStatus = HealthStatus.ALIVE;
            combatant.setHealthStatus(healthStatus);
            int basicMove = combatant.getGameChar().getBasicMove();
            int encumbranceLevel = combatant.getGameChar().getEncumbranceLevel();
            int currentMove = utils.getCurrentMove(healthStatus, basicMove, encumbranceLevel);
            combatant.setCurrentMove(currentMove);
            combatant.setShockPenalty(0);
            combatant.setActionType(ActionType.DO_NOTHING);
            combatant.getCombatMelees().clear();
            combatant.setCombatRanged(null);
            combatant.getCombatDefenses().clear();
        }

        // Set next step to begin combat.
        NextStep nextStep = new NextStep();
        nextStep.setRound(1);
        nextStep.setIndex(0);
        nextStep.setCombatPhase(CombatPhase.BEGIN_TURN);
        battle.setNextStep(nextStep);
        battleService.updateBattle(battle, "Combat started.");
    }

    public NextStep nextStep(CombatPhase combatPhase, ActionType actionType, String targetLabel, String weaponName,
            String modeName, Integer speedAndRange, Integer toHitRoll, DefenseType defenseType,
            String defendingItemName, Integer toDefendRoll, Integer forDamageRoll, Integer forDeathCheckRoll,
            Integer forUnconsciousnessCheckRoll) {
        LOGGER.debug("Next step in combat.");
        Battle battle = battleService.getBattle();
        if (battle.getNextStep() == null) {
            throw new LoggingException(LOGGER, "Combat has not started.");
        }
        if (combatPhase == null) {
            throw new LoggingException(LOGGER, "Combat phase may not be blank.");
        } else if (combatPhase != battle.getNextStep().getCombatPhase()) {
            throw new LoggingException(LOGGER,
                    "Invalid combat phase " + combatPhase + ". Does not match combat phase " +
                            battle.getNextStep().getCombatPhase() + " of current battle.");
        }

        List<Combatant> combatants = battle.getCombatants();
        int round = battle.getNextStep().getRound();
        int index = battle.getNextStep().getIndex();

        // Get current attacker.
        Combatant attacker = combatants.get(index);

        // Get target from current attacker.
        Combatant target = null;
        if (!attacker.getCombatMelees().isEmpty()) {
            String label = attacker.getCombatMelees().get(0).getTargetLabel();
            target = utils.getCombatant(label, combatants);
        } else if (attacker.getCombatRanged() != null) {
            String label = attacker.getCombatRanged().getTargetLabel();
            target = utils.getCombatant(label, combatants);
        }

        NextStep nextStep = null;
        String message = null;
        switch (combatPhase) {
            case BEGIN_TURN:
                beginTurn.updateAttacker(attacker);
                nextStep = beginTurn.resolve(round, index, attacker);
                message = "Begin turn for " + attacker.getLabel() + ".";
                break;
            case PROMPT_FOR_UNCONSCIOUSNESS_CHECK:
                nextStep = unconsciousnessCheck.prompt(round, index, attacker);
                message = "Combatant " + attacker.getLabel() + " must make a health roll to remain conscious.";
                break;
            case RESOLVE_UNCONSCIOUSNESS_CHECK:
                unconsciousnessCheck.validate(forUnconsciousnessCheckRoll);
                unconsciousnessCheck.updateAttacker(attacker, forUnconsciousnessCheckRoll);
                nextStep = unconsciousnessCheck.resolve(round, index, attacker, forUnconsciousnessCheckRoll);
                message = "Combatant " + attacker.getLabel() + " has rolled " + forUnconsciousnessCheckRoll +
                        " to remain conscious.";
                break;
            case PROMPT_FOR_ACTION:
                nextStep = action.prompt(round, index, attacker);
                message = "Combatant " + attacker.getLabel() + " must chose an action.";
                break;
            case RESOLVE_ACTION:
                action.validate(actionType);
                action.updateAttacker(attacker, actionType);
                nextStep = action.resolve(round, index, attacker);
                message = "Combatant " + attacker.getLabel() + " has chosen an action of " + actionType + ".";
                break;
            case PROMPT_FOR_MELEE_TARGET:
                nextStep = meleeTarget.prompt(round, index, attacker);
                message = "Combatant " + attacker.getLabel() + " must a chose a target, melee weapon, and weapon mode.";
                break;
            case RESOLVE_MELEE_TARGET:
                meleeTarget.validate(targetLabel, weaponName, modeName, attacker, combatants);
                meleeTarget.updateAttacker(attacker, targetLabel, weaponName, modeName);
                nextStep = meleeTarget.resolve(round, index, attacker);
                message = "Combatant " + attacker.getLabel() + " has chosen a target of  " + targetLabel +
                        " and a melee weapon of " + weaponName + " with a mode of " + modeName + ".";
                break;
            case PROMPT_FOR_AIM_TARGET:
                nextStep = aimTarget.prompt(round, index, attacker);
                message = "Combatant " + attacker.getLabel() + " must a chose a target and ranged weapon.";
                break;
            case RESOLVE_AIM_TARGET:
                aimTarget.validate(targetLabel, weaponName, attacker, combatants);
                aimTarget.updateAttacker(attacker, targetLabel, weaponName);
                nextStep = aimTarget.resolve(round, index, attacker);
                message = "Combatant " + attacker.getLabel() + " has chosen a target of  " + targetLabel +
                        " and a ranged weapon of " + weaponName + ".";
                break;
            case PROMPT_FOR_RANGED_TARGET:
                nextStep = rangedTarget.prompt(round, index, attacker);
                message = "Combatant " + attacker.getLabel() +
                        " must a chose a target and ranged weapon, and specify the range to & speed of the target.";
                break;
            case RESOLVE_RANGED_TARGET:
                rangedTarget.validate(targetLabel, weaponName, speedAndRange, attacker, combatants);
                rangedTarget.updateAttacker(attacker, targetLabel, weaponName, speedAndRange);
                nextStep = rangedTarget.resolve(round, index, attacker);
                message = "Combatant " + attacker.getLabel() + " has chosen a target of  " + targetLabel +
                        " and a ranged weapon of " + weaponName + " at a range speed of " + speedAndRange + ".";
                break;
            case PROMPT_FOR_TO_HIT:
                nextStep = toHit.promptAndUpdateAttacker(round, index, attacker);
                message = "Combatant " + attacker.getLabel() + " must roll to hit.";
                break;
            case RESOLVE_TO_HIT:
                toHit.validate(toHitRoll);
                toHit.updateAttacker(attacker, toHitRoll);
                nextStep = toHit.resolve(round, index, attacker, target);
                message = "Combatant " + attacker.getLabel() + " has rolled " + toHitRoll + " to hit.";
                break;
            case PROMPT_FOR_DEFENSE:
                nextStep = defense.prompt(round, index, target);
                message = "Target " + target.getLabel() + " must chose a defense and item (if needed for defense).";
                break;
            case RESOLVE_DEFENSE:
                defense.validate(defenseType, defendingItemName, target);
                defense.updateTarget(target, defenseType, defendingItemName);
                nextStep = defense.resolve(round, index, target);
                message = "Target " + target.getLabel() + " has chosen a defense of " + defenseType + " using item " +
                        defendingItemName + ".";
                break;
            case PROMPT_FOR_TO_DEFEND:
                nextStep = toDefend.promptAndUpdateTarget(round, index, target);
                message = "Target " + target.getLabel() + " must roll to defend.";
                break;
            case RESOLVE_TO_DEFEND:
                toDefend.validate(toDefendRoll);
                toDefend.updateTarget(target, toDefendRoll);
                nextStep = toDefend.resolve(round, index, attacker, target);
                message = "Target " + target.getLabel() + " has rolled " + toDefendRoll + " to defend.";
                break;
            case PROMPT_FOR_DAMAGE:
                nextStep = forDamage.promptAndUpdateAttacker(round, index, attacker);
                message = "Combatant " + attacker.getLabel() + " must roll damage.";
                break;
            case RESOLVE_DAMAGE:
                forDamage.validate(forDamageRoll, attacker);
                forDamage.updateAttacker(attacker, forDamageRoll, target);
                forDamage.updateTarget(target, attacker);
                nextStep = forDamage.resolve(round, index, attacker, target);
                message = "Target " + target.getLabel() + " was hit with " + forDamageRoll + " damage.";
                break;
            case PROMPT_FOR_DEATH_CHECK:
                nextStep = deathCheck.prompt(round, index, target);
                message = "Target " + target.getLabel() + " must make a health roll to avoid dying.";
                break;
            case RESOLVE_DEATH_CHECK:
                deathCheck.validate(forDeathCheckRoll);
                deathCheck.updateTarget(target, forDeathCheckRoll);
                nextStep = deathCheck.resolve(round, index, target, forDeathCheckRoll);
                message = "Target " + target.getLabel() + " rolled " + forDeathCheckRoll + " to avoid dying.";
                break;
            case END_TURN:
                endTurn.updateAttacker(attacker);
                nextStep = endTurn.resolve(round, index, combatants.size());
                message = "End turn for " + attacker.getLabel() + ".";
                break;
        }
        battle.setNextStep(nextStep);
        battleService.updateBattle(battle, message);
        return battle.getNextStep();
    }
}
