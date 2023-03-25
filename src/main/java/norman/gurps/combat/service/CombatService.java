package norman.gurps.combat.service;

import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.ActionType;
import norman.gurps.combat.model.ArmorPiece;
import norman.gurps.combat.model.Battle;
import norman.gurps.combat.model.CombatDefense;
import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.DamageType;
import norman.gurps.combat.model.DefenseType;
import norman.gurps.combat.model.HealthStatus;
import norman.gurps.combat.model.HitLocation;
import norman.gurps.combat.model.MeleeWeapon;
import norman.gurps.combat.model.MeleeWeaponMode;
import norman.gurps.combat.model.NextStep;
import norman.gurps.combat.model.ResultType;
import norman.gurps.combat.model.Shield;
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

    public CombatService(BattleService battleService) {
        this.battleService = battleService;
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

        Function<Combatant, Double> bs = combatant -> combatant.getGameChar().getBasicSpeed();
        Function<Combatant, Integer> dx = combatant -> combatant.getGameChar().getDexterity();
        Comparator<Combatant> comp = Comparator.comparing(bs).thenComparing(dx).reversed();
        battle.getCombatants().sort(comp);

        for (Combatant combatant : battle.getCombatants()) {
            combatant.setCurrentDamage(0);
            combatant.setPreviousDamage(0);
            combatant.setNbrOfDeathChecksNeeded(0);
            combatant.setDeathCheckFailed(false);
            HealthStatus healthStatus = HealthStatus.ALIVE;
            combatant.setHealthStatus(healthStatus);
            int basicMove = combatant.getGameChar().getBasicMove();
            int encumbranceLevel = combatant.getGameChar().getEncumbranceLevel();
            int currentMove = getCurrentMove(healthStatus, basicMove, encumbranceLevel);
            combatant.setCurrentMove(currentMove);
            combatant.setActionType(ActionType.DO_NOTHING);
        }

        NextStep nextStep = new NextStep();
        nextStep.setRound(1);
        nextStep.setIndex(0);
        nextStep.setCombatPhase(CombatPhase.BEGIN);
        battle.setNextStep(nextStep);
        battleService.updateBattle(battle, "Combat started.");
    }

    public NextStep nextStep(CombatPhase combatPhase, ActionType actionType, String targetLabel, String weaponName,
            String modeName, Integer toHitRoll, DefenseType defenseType, String defendingItemName, Integer toDefendRoll,
            Integer forDamageRoll, Integer forDeathCheckRoll, Integer forUnconsciousnessCheckRoll) {
        LOGGER.debug("Next step in combat.");
        Battle battle = battleService.getBattle();
        if (battle.getNextStep() == null) {
            throw new LoggingException(LOGGER, "Combat has not started.");
        }
        if (combatPhase == null) {
            throw new LoggingException(LOGGER, "Combat phase may not be blank.");
        } else if (combatPhase != battle.getNextStep().getCombatPhase()) {
            throw new LoggingException(LOGGER,
                    "Invalid phase " + combatPhase + ". Does not match phase " + battle.getNextStep().getCombatPhase() +
                            " of current battle.");
        }

        int round = battle.getNextStep().getRound();
        int index = battle.getNextStep().getIndex();

        Combatant combatant = battle.getCombatants().get(index);
        Combatant target = null;
        if (combatant.getTargetLabel() != null) {
            target = getCombatant(combatant.getTargetLabel(), battle.getCombatants());
        }

        NextStep nextStep = null;
        switch (combatPhase) {
            case BEGIN:
                nextStep = doBegin(round, index, combatant);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle, "Begin next turn for " + combatant.getLabel() + ".");
                break;
            case PROMPT_FOR_UNCONSCIOUSNESS_CHECK:
                nextStep = doPromptForUnconsciousnessCheck(round, index, combatant);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle,
                        "Combatant " + combatant.getLabel() + " must make a health roll to remain conscious.");
                break;
            case RESOLVE_UNCONSCIOUSNESS_CHECK:
                validateResolveUnconsciousnessCheck(forUnconsciousnessCheckRoll);
                nextStep = doResolveUnconsciousnessCheck(round, index, combatant, forUnconsciousnessCheckRoll);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle,
                        "Combatant " + combatant.getLabel() + " has rolled " + forUnconsciousnessCheckRoll +
                                " to remain conscious.");
                break;
            case PROMPT_FOR_ACTION:
                nextStep = doPromptForActionType(round, index, combatant);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle, "Combatant " + combatant.getLabel() + " must chose an action.");
                break;
            case RESOLVE_ACTION:
                validateResolveActionType(actionType);
                nextStep = doResolveActionType(round, index, combatant, actionType);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle,
                        "Combatant " + combatant.getLabel() + " has chosen an action of " + actionType + ".");
                break;
            case PROMPT_FOR_TARGET_AND_WEAPON:
                nextStep = doPromptForTargetAndWeapon(round, index, combatant);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle,
                        "Combatant " + combatant.getLabel() + " must chose a target and weapon/mode.");
                break;
            case RESOLVE_TARGET_AND_WEAPON:
                validateResolveTargetAndWeapon(battle, combatant, targetLabel, weaponName, modeName);
                nextStep = doResolveTargetAndWeapon(round, index, combatant, targetLabel, weaponName, modeName);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle,
                        "Combatant " + combatant.getLabel() + " has chosen to attack " + targetLabel + " with " +
                                weaponName + "/" + modeName + ".");
                break;
            case PROMPT_FOR_TO_HIT:
                nextStep = doPromptForToHit(round, index, combatant);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle, "Combatant " + combatant.getLabel() + " must roll to hit.");
                break;
            case RESOLVE_TO_HIT:
                validateResolveToHit(toHitRoll);
                nextStep = doResolveToHit(round, index, combatant, target, toHitRoll);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle,
                        "Combatant " + combatant.getLabel() + " has rolled " + toHitRoll + " to hit.");
                break;
            case PROMPT_FOR_DEFENSE:
                nextStep = doPromptForDefense(round, index, target);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle, "Target " + target.getLabel() +
                        " must chose a defense and (if needed) an item to defend with.");
                break;
            case RESOLVE_DEFENSE:
                validateResolveDefense(target, defenseType, defendingItemName);
                nextStep = doResolveDefense(round, index, target, defenseType, defendingItemName);
                battle.setNextStep(nextStep);
                String itemParenth = "";
                if (defenseType != DefenseType.DODGE && defenseType != DefenseType.NO_DEFENSE) {
                    itemParenth = " (" + defendingItemName + ")";
                }
                battleService.updateBattle(battle,
                        "Target " + target.getLabel() + " has chosen a defense of " + defenseType + itemParenth + ".");
                break;
            case PROMPT_FOR_TO_DEFEND:
                nextStep = doPromptForToDefend(round, index, target);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle, "Target " + target.getLabel() + " must roll to defend.");
                break;
            case RESOLVE_TO_DEFEND:
                validateResolveToDefend(toDefendRoll);
                nextStep = doResolveToDefend(round, index, combatant, target, toDefendRoll);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle,
                        "Target " + target.getLabel() + " has rolled " + toDefendRoll + " to defend.");
                break;
            case PROMPT_FOR_DAMAGE:
                nextStep = doPromptForDamage(round, index, combatant);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle, "Combatant " + combatant.getLabel() + " must roll for damage.");
                break;
            case RESOLVE_DAMAGE:
                validateResolveDamage(forDamageRoll);
                nextStep = doResolveDamage(round, index, combatant, target, forDamageRoll);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle,
                        "Target " + target.getLabel() + " was hit with " + forDamageRoll + ".");
                break;
            case PROMPT_FOR_DEATH_CHECK:
                nextStep = doPromptForDeathCheck(round, index, target);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle,
                        "Target " + target.getLabel() + " must make a health roll to avoid dying.");
                break;
            case RESOLVE_DEATH_CHECK:
                validateResolveDeathCheck(forDeathCheckRoll);
                nextStep = doResolveDeathCheck(round, index, target, forDeathCheckRoll);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle,
                        "Target " + target.getLabel() + " rolled " + forDeathCheckRoll + " to avoid dying.");
                break;
            case END:
                nextStep = doEnd(round, index, combatant, battle.getCombatants().size());
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle, "End turn for " + combatant.getLabel() + ".");
                break;
        }
        return battle.getNextStep();
    }

    private NextStep doBegin(int round, int index, Combatant combatant) {
        HealthStatus healthStatus = combatant.getHealthStatus();
        CombatPhase combatPhase;
        String message;
        if (healthStatus == HealthStatus.ALIVE || healthStatus == HealthStatus.REELING) {
            combatPhase = CombatPhase.PROMPT_FOR_ACTION;
            message = "It is now " + combatant.getLabel() + "'s turn. He/she is " + healthStatus + ".";
        } else if (healthStatus == HealthStatus.BARELY || healthStatus == HealthStatus.ALMOST ||
                healthStatus == HealthStatus.ALMOST2 || healthStatus == HealthStatus.ALMOST3 ||
                healthStatus == HealthStatus.ALMOST4) {
            combatPhase = CombatPhase.PROMPT_FOR_UNCONSCIOUSNESS_CHECK;
            message = "It is now " + combatant.getLabel() + "'s turn. He/she is " + healthStatus + ".";
        } else {
            combatPhase = CombatPhase.END;
            message = combatant.getLabel() + " is " + healthStatus + ". His/her turn is being skipped.";
        }

        // Initialize combatant.
        combatant.setActionType(null);
        combatant.setTargetLabel(null);
        combatant.setWeaponName(null);
        combatant.setModeName(null);
        combatant.setToHitEffectiveSkill(null);
        combatant.setToHitRoll(null);
        combatant.setToHitResultType(null);
        combatant.setDamageDice(null);
        combatant.setDamageAdds(null);
        combatant.setForDamageRoll(null);
        combatant.getCombatDefenses().clear();

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(combatPhase);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doPromptForUnconsciousnessCheck(int round, int index, Combatant combatant) {
        int check = combatant.getGameChar().getUnconsciousnessCheck();
        String message =
                combatant.getLabel() + " needs to make a roll to stay conscious. Please roll 3d (need " + check + ").";

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(CombatPhase.RESOLVE_UNCONSCIOUSNESS_CHECK);
        nextStep.setInputNeeded(true);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doPromptForActionType(int round, int index, Combatant combatant) {
        String message = combatant.getLabel() + ", please chose an action.";

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(CombatPhase.RESOLVE_ACTION);
        nextStep.setInputNeeded(true);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doPromptForTargetAndWeapon(int round, int index, Combatant combatant) {
        String message = combatant.getLabel() + ", please chose a target and a weapon/mode.";

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(CombatPhase.RESOLVE_TARGET_AND_WEAPON);
        nextStep.setInputNeeded(true);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doPromptForToHit(int round, int index, Combatant combatant) {
        String weaponName = combatant.getWeaponName();
        MeleeWeapon weapon = getMeleeWeapon(weaponName, combatant.getGameChar().getMeleeWeapons());
        int skill = weapon.getSkill();
        if (weapon.getMinStrength() > combatant.getGameChar().getStrength()) {
            skill -= (weapon.getMinStrength() - combatant.getGameChar().getStrength());
        }
        String message = combatant.getLabel() + ", please roll 3d (need " + skill + " to hit).";

        // Update combatant.
        combatant.setToHitEffectiveSkill(skill);

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(CombatPhase.RESOLVE_TO_HIT);
        nextStep.setInputNeeded(true);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doPromptForDefense(int round, int index, Combatant target) {
        String message = target.getLabel() + ", please chose a defense and item (if needed for defense).";

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(CombatPhase.RESOLVE_DEFENSE);
        nextStep.setInputNeeded(true);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doPromptForToDefend(int round, int index, Combatant target) {
        int size = target.getCombatDefenses().size();
        CombatDefense combatDefense = target.getCombatDefenses().get(size - 1);
        int skill = 0;
        DefenseType defenseType = combatDefense.getDefenseType();
        if (defenseType == DefenseType.PARRY) {
            MeleeWeapon weapon = getMeleeWeapon(combatDefense.getDefendingItemName(),
                    target.getGameChar().getMeleeWeapons());
            int weaponSkill = weapon.getSkill();
            if (weapon.getMinStrength() > target.getGameChar().getStrength()) {
                weaponSkill -= (weapon.getMinStrength() - target.getGameChar().getStrength());
            }
            skill = (weaponSkill / 2) + 3 + weapon.getParryModifier();
        } else if (defenseType == DefenseType.BLOCK) {
            Shield shield = getShield(combatDefense.getDefendingItemName(), target.getGameChar().getShields());
            int shieldSkill = shield.getSkill();
            skill = (shieldSkill / 2) + 3;
        } else if (defenseType == DefenseType.DODGE) {
            skill = target.getCurrentMove() + 3;
        }
        if (!target.getGameChar().getShields().isEmpty()) {
            skill += target.getGameChar().getShields().get(0).getDefenseBonus();
        }
        String message = target.getLabel() + ", please roll 3d (need " + skill + " to defend).";

        // Update target.
        combatDefense.setToDefendEffectiveSkill(skill);

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(CombatPhase.RESOLVE_TO_DEFEND);
        nextStep.setInputNeeded(true);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doPromptForDamage(int round, int index, Combatant combatant) {
        MeleeWeapon weapon = getMeleeWeapon(combatant.getWeaponName(), combatant.getGameChar().getMeleeWeapons());
        MeleeWeaponMode mode = getWeaponMode(combatant.getModeName(), weapon.getMeleeWeaponModes());
        String dmgDesc = getDamageDescription(mode.getDamageDice(), mode.getDamageAdds(), mode.getDamageType());
        String message = combatant.getLabel() + ", please roll " + dmgDesc + " damage.";

        // Update combatant.
        combatant.setDamageDice(mode.getDamageDice());
        combatant.setDamageAdds(mode.getDamageAdds());

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(CombatPhase.RESOLVE_DAMAGE);
        nextStep.setInputNeeded(true);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doPromptForDeathCheck(int round, int index, Combatant target) {
        int check = target.getGameChar().getDeathCheck();
        int nbr = target.getNbrOfDeathChecksNeeded();
        String message;
        if (nbr > 1) {
            message = target.getLabel() + " needs to make " + nbr + " rolls to avoid death. Please roll 3d (need " +
                    check + ").";
        } else {
            message = target.getLabel() + " needs to make a roll to avoid death. Please roll 3d (need " + check + ").";
        }

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(CombatPhase.RESOLVE_DEATH_CHECK);
        nextStep.setInputNeeded(true);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void validateResolveUnconsciousnessCheck(Integer rollForUnconsciousnessCheck) {
        // Validate roll.
        if (rollForUnconsciousnessCheck == null) {
            throw new LoggingException(LOGGER, "Value rolled to remain conscious may not be blank.");
        }
    }

    private void validateResolveActionType(ActionType action) {
        // Validate action.
        if (action == null) {
            throw new LoggingException(LOGGER, "ActionType may not be blank.");
        }
    }

    private void validateResolveTargetAndWeapon(Battle battle, Combatant combatant, String targetLabel,
            String weaponName, String modeName) {
        // Validate target.
        if (targetLabel == null) {
            throw new LoggingException(LOGGER, "Target may not be blank.");
        } else if (targetLabel.equals(combatant.getLabel())) {
            throw new LoggingException(LOGGER, "Combatant " + combatant.getLabel() + " may not target him/herself.");
        } else {
            if (getCombatant(targetLabel, battle.getCombatants()) == null) {
                throw new LoggingException(LOGGER,
                        "Target " + targetLabel + " is not a combatant in the current battle.");
            }
        }

        // Validate weapon/mode.
        if (weaponName == null) {
            throw new LoggingException(LOGGER, "Weapon may not be blank.");
        } else {
            MeleeWeapon weapon = getMeleeWeapon(weaponName, combatant.getGameChar().getMeleeWeapons());
            if (weapon == null) {
                throw new LoggingException(LOGGER,
                        "Weapon " + weaponName + " is not a ready weapon for combatant " + combatant.getLabel() + ".");
            } else if (modeName == null) {
                throw new LoggingException(LOGGER, "Weapon mode may not be blank.");
            } else {
                if (getWeaponMode(modeName, weapon.getMeleeWeaponModes()) == null) {
                    throw new LoggingException(LOGGER,
                            "Mode " + modeName + " is not a valid mode of weapon " + weapon.getName() +
                                    " for combatant " + combatant.getLabel() + ".");
                }
            }
        }
    }

    private void validateResolveToHit(Integer rollToHit) {
        // Validate roll.
        if (rollToHit == null) {
            throw new LoggingException(LOGGER, "Value rolled to hit may not be blank.");
        }
    }

    private void validateResolveDefense(Combatant target, DefenseType defenseType, String defendingItemName) {
        // Validate defense.
        if (defenseType == null) {
            throw new LoggingException(LOGGER, "Defense may not be blank.");
        }

        // Validate defending item name.
        if (defenseType == DefenseType.PARRY || defenseType == DefenseType.BLOCK) {
            if (defendingItemName == null) {
                throw new LoggingException(LOGGER,
                        "If Defense is PARRY or BLOCK, defending item name may not be blank.");
            }
        } else {
            if (defendingItemName != null) {
                throw new LoggingException(LOGGER,
                        "If Defense is not PARRY and not BLOCK, defending item name must not be blank.");
            }
        }

        // Validate weapon for parry.
        if (defenseType == DefenseType.PARRY) {
            if (getMeleeWeapon(defendingItemName, target.getGameChar().getMeleeWeapons()) == null) {
                throw new LoggingException(LOGGER,
                        "Weapon " + defendingItemName + " is not a ready parrying weapon for combatant " +
                                target.getLabel() + ".");
            }
        }

        // Validate shield for block.
        if (defenseType == DefenseType.BLOCK) {
            if (getShield(defendingItemName, target.getGameChar().getShields()) == null) {
                throw new LoggingException(LOGGER,
                        "Shield " + defendingItemName + " is not a ready shield for combatant " + target.getLabel() +
                                ".");
            }
        }
    }

    private void validateResolveToDefend(Integer rollToDefend) {
        // Validate roll.
        if (rollToDefend == null) {
            throw new LoggingException(LOGGER, "Value rolled to defend may not be blank.");
        }
    }

    private void validateResolveDamage(Integer rollForDamage) {
        // Validate roll.
        if (rollForDamage == null) {
            throw new LoggingException(LOGGER, "Value rolled for damage may not be blank.");
        } else if (rollForDamage < 0) {
            throw new LoggingException(LOGGER, "Value rolled for damage may not be less than zero.");
        }
    }

    private void validateResolveDeathCheck(Integer rollForDeathCheck) {
        // Validate roll.
        if (rollForDeathCheck == null) {
            throw new LoggingException(LOGGER, "Value rolled to avoid death may not be blank.");
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private NextStep doResolveUnconsciousnessCheck(int round, int index, Combatant combatant,
            int rollForUnconsciousnessCheck) {
        int check = combatant.getGameChar().getUnconsciousnessCheck();
        ResultType result = getResultType(check, rollForUnconsciousnessCheck);
        boolean failed = false;
        HealthStatus status = combatant.getHealthStatus();
        int move = combatant.getCurrentMove();
        CombatPhase phase;
        String message;
        if (result == ResultType.CRITICAL_SUCCESS || result == ResultType.SUCCESS) {
            phase = CombatPhase.PROMPT_FOR_ACTION;
            message =
                    combatant.getLabel() + " successfully remained conscious! Rolled a " + rollForUnconsciousnessCheck +
                            ", needed a " + check + ".";
        } else {
            failed = true;
            status = HealthStatus.UNCONSCIOUS;
            move = 0;
            phase = CombatPhase.END;
            message = combatant.getLabel() + " is now unconscious! Rolled a " + rollForUnconsciousnessCheck +
                    ", needed a " + check + ".";
        }

        // Update combatant.
        combatant.setUnconsciousnessCheckFailed(failed);
        combatant.setHealthStatus(status);
        combatant.setCurrentMove(move);

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(phase);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doResolveActionType(int round, int index, Combatant combatant, ActionType action) {
        CombatPhase phase;
        if (action == ActionType.ATTACK) {
            phase = CombatPhase.PROMPT_FOR_TARGET_AND_WEAPON;
        } else {
            phase = CombatPhase.END;
        }
        String message = combatant.getLabel() + " has chosen action " + action + ".";

        // Update combatant.
        combatant.setActionType(action);

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(phase);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doResolveTargetAndWeapon(int round, int index, Combatant combatant, String targetLabel,
            String weaponName, String modeName) {
        String message =
                combatant.getLabel() + " has chosen to attack " + targetLabel + " with " + weaponName + "/" + modeName +
                        ".";

        // Update combatant.
        combatant.setTargetLabel(targetLabel);
        combatant.setWeaponName(weaponName);
        combatant.setModeName(modeName);

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(CombatPhase.PROMPT_FOR_TO_HIT);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doResolveToHit(int round, int index, Combatant combatant, Combatant target, int toHitRoll) {
        int skill = combatant.getToHitEffectiveSkill();
        ResultType result = getResultType(skill, toHitRoll);
        String weaponName = combatant.getWeaponName();
        String modeName = combatant.getModeName();
        CombatPhase combatPhase;
        String message;
        if (result == ResultType.CRITICAL_SUCCESS || result == ResultType.SUCCESS) {
            combatPhase = CombatPhase.PROMPT_FOR_DEFENSE;
            message =
                    combatant.getLabel() + " successfully attacked " + target.getLabel() + " with " + weaponName + "/" +
                            modeName + ". Rolled a " + toHitRoll + ", needed a " + skill + ".";
        } else {
            combatPhase = CombatPhase.END;
            message = combatant.getLabel() + " attacked " + target.getLabel() + " with " + weaponName + "/" + modeName +
                    ", but failed to hit. Rolled a " + toHitRoll + ", but needed a " + skill + ".";
        }

        // Update combatant.
        combatant.setToHitRoll(toHitRoll);
        combatant.setToHitResultType(result);

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(combatPhase);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doResolveDefense(int round, int index, Combatant target, DefenseType type, String item) {
        String itemParenth = "";
        if (type != DefenseType.DODGE && type != DefenseType.NO_DEFENSE) {
            itemParenth = " (" + item + ")";
        }
        String message = target.getLabel() + " has chosen a defense of " + type + itemParenth + ".";

        // Update target.
        CombatDefense defense = new CombatDefense();
        defense.setDefenseType(type);
        defense.setDefendingItemName(item);
        target.getCombatDefenses().add(defense);

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(CombatPhase.PROMPT_FOR_TO_DEFEND);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doResolveToDefend(int round, int index, Combatant combatant, Combatant target, int rollToDefend) {
        int size = target.getCombatDefenses().size();
        CombatDefense defense = target.getCombatDefenses().get(size - 1);
        int skill = defense.getToDefendEffectiveSkill();
        ResultType result = getResultType(skill, rollToDefend);
        DefenseType type = defense.getDefenseType();
        String item = defense.getDefendingItemName();
        String itemParenth = "";
        if (type != DefenseType.DODGE && type != DefenseType.NO_DEFENSE) {
            itemParenth = " (" + item + ")";
        }
        CombatPhase phase;
        String message;
        if (result == ResultType.CRITICAL_SUCCESS || result == ResultType.SUCCESS) {
            phase = CombatPhase.END;
            message = target.getLabel() + " successfully defended against " + combatant.getLabel() + " with " + type +
                    itemParenth + ". Rolled a " + rollToDefend + ", needed a " + skill + ".";
        } else {
            phase = CombatPhase.PROMPT_FOR_DAMAGE;
            message = target.getLabel() + " failed to defend against " + combatant.getLabel() + " with " + type +
                    itemParenth + ". Rolled a " + rollToDefend + ", but needed a " + skill + ".";
        }

        // Update target.
        defense.setToDefendRoll(rollToDefend);
        defense.setToDefendResult(result);

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(phase);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doResolveDamage(int round, int index, Combatant combatant, Combatant target, int rollForDamage) {
        ArmorPiece armorPiece = getArmorPiece(HitLocation.TORSO, target.getGameChar().getArmorPieces());
        int dr = armorPiece.getDamageResistance();
        int penetratingDamage = rollForDamage - dr;
        MeleeWeapon weapon = getMeleeWeapon(combatant.getWeaponName(), combatant.getGameChar().getMeleeWeapons());
        MeleeWeaponMode mode = getWeaponMode(combatant.getModeName(), weapon.getMeleeWeaponModes());
        DamageType damageType = mode.getDamageType();
        double mult = getDamageMultiplier(damageType);
        int injuryDamage = (int) (penetratingDamage * mult);
        if (penetratingDamage > 0 && injuryDamage == 0) {
            injuryDamage = 1;
        }
        int hitPoints = target.getGameChar().getHitPoints();
        int previousDamage = target.getPreviousDamage();
        int oldCurrentDamage = target.getCurrentDamage();
        int newCurrentDamage = oldCurrentDamage + injuryDamage;
        int oldRemainingHitPoints = hitPoints - oldCurrentDamage - previousDamage;
        int newRemainingHitPoints = hitPoints - newCurrentDamage - previousDamage;
        int oldHitLevel = getHitLevel(hitPoints, oldRemainingHitPoints);
        int newHitLevel = getHitLevel(hitPoints, newRemainingHitPoints);
        Boolean deathCheckFailed = target.getDeathCheckFailed();
        HealthStatus newStatus = getHealthStatus(newHitLevel, deathCheckFailed);
        int basicMove = target.getGameChar().getBasicMove();
        int encumbranceLevel = target.getGameChar().getEncumbranceLevel();
        int newCurrentMove = getCurrentMove(newStatus, basicMove, encumbranceLevel);

        // If the target's new status is between ALMOST and ALMOST4 and the target's old status is less and the target
        // has not yet failed a death check, then the target will need to make one or more rolls to avoid death.
        int needed = 0;
        if (newHitLevel >= 4 && newHitLevel <= 7 && newHitLevel > oldHitLevel) {
            if (oldHitLevel <= 3) {
                oldHitLevel = 3;
            }
            needed = newHitLevel - oldHitLevel;
        }
        CombatPhase combatPhase = CombatPhase.END;
        if (needed > 0) {
            combatPhase = CombatPhase.PROMPT_FOR_DEATH_CHECK;
        }
        String message = target.getLabel() + " is hit for " + rollForDamage + " " + damageType + " damage. " +
                penetratingDamage + " gets through his/her armour (DR " + dr + ") doing " + injuryDamage +
                " hits of damage.";

        // Update combatant.
        combatant.setForDamageRoll(rollForDamage);

        // Update target.
        target.setCurrentDamage(newCurrentDamage);
        target.setNbrOfDeathChecksNeeded(needed);
        target.setHealthStatus(newStatus);
        target.setCurrentMove(newCurrentMove);

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(combatPhase);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doResolveDeathCheck(int round, int index, Combatant target, int rollForDeathCheck) {
        int check = target.getGameChar().getDeathCheck();
        ResultType resultType = getResultType(check, rollForDeathCheck);
        int nbr = target.getNbrOfDeathChecksNeeded();
        boolean failed = false;
        HealthStatus healthStatus = target.getHealthStatus();
        int move = target.getCurrentMove();
        CombatPhase combatPhase = CombatPhase.END;
        String message;
        if (resultType == ResultType.CRITICAL_SUCCESS || resultType == ResultType.SUCCESS) {
            nbr--;
            if (nbr > 0) {
                combatPhase = CombatPhase.PROMPT_FOR_DEATH_CHECK;
            }
            message = target.getLabel() + " successfully avoided death! Rolled a " + rollForDeathCheck + ", needed a " +
                    check + ".";
        } else {
            nbr = 0;
            failed = true;
            healthStatus = HealthStatus.DEAD;
            move = 0;
            message = target.getLabel() + " is now dead! Rolled a " + rollForDeathCheck + ", needed a " + check + ".";
        }

        // Update target.
        target.setNbrOfDeathChecksNeeded(nbr);
        target.setDeathCheckFailed(failed);
        target.setHealthStatus(healthStatus);
        target.setCurrentMove(move);

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(combatPhase);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doEnd(int round, int index, Combatant combatant, int nbrOfCombatants) {
        // Update combatant.
        int currentDamage = combatant.getCurrentDamage();
        int previousDamage = combatant.getPreviousDamage();
        combatant.setCurrentDamage(0);
        combatant.setPreviousDamage(currentDamage + previousDamage);

        // Create next step.
        NextStep nextStep = new NextStep();
        int newRound = round;
        int newIndex = index + 1;
        if (newIndex >= nbrOfCombatants) {
            newRound = round + 1;
            newIndex = 0;
        }
        nextStep.setRound(newRound);
        nextStep.setIndex(newIndex);
        nextStep.setCombatPhase(CombatPhase.BEGIN);
        nextStep.setInputNeeded(false);
        return nextStep;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private ArmorPiece getArmorPiece(HitLocation location, List<ArmorPiece> armorPieces) {
        ArmorPiece found = null;
        for (ArmorPiece armorPiece : armorPieces) {
            if (armorPiece.getHitLocation() == location) {
                found = armorPiece;
            }
        }
        return found;
    }

    private Combatant getCombatant(String label, List<Combatant> combatants) {
        Combatant found = null;
        for (Combatant combatant : combatants) {
            if (label.equals(combatant.getLabel())) {
                found = combatant;
            }
        }
        return found;
    }

    private int getCurrentMove(HealthStatus status, int basicMove, int encumbranceLevel) {
        int currentMove = 0;
        if (status == HealthStatus.ALIVE) {
            currentMove = basicMove - encumbranceLevel;
        } else if (status == HealthStatus.REELING || status == HealthStatus.BARELY || status == HealthStatus.ALMOST ||
                status == HealthStatus.ALMOST2 || status == HealthStatus.ALMOST3 || status == HealthStatus.ALMOST4) {
            currentMove = (int) Math.ceil(((double) basicMove - (double) encumbranceLevel) / 2.0);
        }
        return currentMove;
    }

    private String getDamageDescription(int damageDice, int damageAdds, DamageType damageType) {
        StringBuilder sb = null;
        if (damageDice != 0) {
            sb = new StringBuilder(String.valueOf(damageDice));
            sb.append("d");
        }
        if (damageAdds != 0) {
            if (sb == null) {
                sb = new StringBuilder(String.valueOf(damageAdds));
            } else {
                if (damageAdds > 0) {
                    sb.append("+");
                }
                sb.append(damageAdds);
            }
        }
        if (damageType != null) {
            if (sb == null) {
                sb = new StringBuilder(damageType.toString());
            } else {
                sb.append(" ");
                sb.append(damageType);
            }
        }
        return sb.toString();
    }

    private double getDamageMultiplier(DamageType damageType) {
        double multiplier = 1.0;
        if (damageType == DamageType.SMALL_PIERCING) {
            multiplier = 0.5;
        } else if (damageType == DamageType.CUTTING || damageType == DamageType.LARGE_PIERCING) {
            multiplier = 1.5;
        } else if (damageType == DamageType.IMPALING || damageType == DamageType.HUGE_PIERCING) {
            multiplier = 2.0;
        }
        return multiplier;
    }

    private int getHitLevel(int hitPoints, int remainingHitPoints) {
        int hitLevel = 0;
        if ((double) remainingHitPoints >= (double) hitPoints / 3.0) {
            hitLevel = 1; // Equivalent to ALIVE.
        } else if (remainingHitPoints > 0) {
            hitLevel = 2; // Equivalent REELING.
        } else if (remainingHitPoints > -1 * hitPoints) {
            hitLevel = 3; // Equivalent to BARELY or UNCONSCIOUS.
        } else if (remainingHitPoints > -2 * hitPoints) {
            hitLevel = 4; // Equivalent to ALMOST or DEAD.
        } else if (remainingHitPoints > -3 * hitPoints) {
            hitLevel = 5; // Equivalent to ALMOST2 or DEAD.
        } else if (remainingHitPoints > -4 * hitPoints) {
            hitLevel = 6; // Equivalent to ALMOST3 or DEAD.
        } else if (remainingHitPoints > -5 * hitPoints) {
            hitLevel = 7; // Equivalent to ALMOST4 or DEAD.
        } else if (remainingHitPoints > -10 * hitPoints) {
            hitLevel = 8; // Equivalent to DEAD.
        } else {
            hitLevel = 9; // Equivalent to DESTROYED;
        }
        return hitLevel;
    }

    private HealthStatus getHealthStatus(int level, boolean deathCheckFailed) {
        HealthStatus healthStatus = null;
        if (level == 1) {
            healthStatus = HealthStatus.ALIVE;
        } else if (level == 2) {
            healthStatus = HealthStatus.REELING;
        } else if (level == 3) {
            healthStatus = deathCheckFailed ? HealthStatus.UNCONSCIOUS : HealthStatus.BARELY;
        } else if (level == 4) {
            healthStatus = deathCheckFailed ? HealthStatus.DEAD : HealthStatus.ALMOST;
        } else if (level == 5) {
            healthStatus = deathCheckFailed ? HealthStatus.DEAD : HealthStatus.ALMOST2;
        } else if (level == 6) {
            healthStatus = deathCheckFailed ? HealthStatus.DEAD : HealthStatus.ALMOST3;
        } else if (level == 7) {
            healthStatus = deathCheckFailed ? HealthStatus.DEAD : HealthStatus.ALMOST4;
        } else if (level == 8) {
            healthStatus = HealthStatus.DEAD;
        } else if (level == 9) {
            healthStatus = HealthStatus.DESTROYED;
        }
        return healthStatus;
    }

    private Shield getShield(String name, List<Shield> shields) {
        Shield found = null;
        for (Shield shield : shields) {
            if (name.equals(shield.getName())) {
                found = shield;
            }
        }
        return found;
    }

    private ResultType getResultType(int skill, int roll) {
        int margin = skill - roll;
        ResultType result;
        if (roll == 3) {
            result = ResultType.CRITICAL_SUCCESS;
        } else if (roll == 4) {
            result = ResultType.CRITICAL_SUCCESS;
        } else if (roll == 5 && skill >= 15) {
            result = ResultType.CRITICAL_SUCCESS;
        } else if (roll == 6 && skill >= 16) {
            result = ResultType.CRITICAL_SUCCESS;
        } else if (roll == 18) {
            result = ResultType.CRITICAL_FAILURE;
        } else if (roll == 17 && skill <= 15) {
            result = ResultType.CRITICAL_FAILURE;
        } else if (margin <= -10) {
            result = ResultType.CRITICAL_FAILURE;
        } else if (margin >= 0) {
            result = ResultType.SUCCESS;
        } else {
            result = ResultType.FAILURE;
        }
        return result;
    }

    private MeleeWeapon getMeleeWeapon(String name, List<MeleeWeapon> weapons) {
        MeleeWeapon found = null;
        for (MeleeWeapon weapon : weapons) {
            if (name.equals(weapon.getName())) {
                found = weapon;
            }
        }
        return found;
    }

    private MeleeWeaponMode getWeaponMode(String name, List<MeleeWeaponMode> modes) {
        MeleeWeaponMode found = null;
        for (MeleeWeaponMode mode : modes) {
            if (name.equals(mode.getName())) {
                found = mode;
            }
        }
        return found;
    }
}
