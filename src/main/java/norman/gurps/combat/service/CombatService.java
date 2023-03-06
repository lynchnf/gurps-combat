package norman.gurps.combat.service;

import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.Action;
import norman.gurps.combat.model.Battle;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.Defense;
import norman.gurps.combat.model.HealthStatus;
import norman.gurps.combat.model.MeleeWeapon;
import norman.gurps.combat.model.MeleeWeaponMode;
import norman.gurps.combat.model.NextStep;
import norman.gurps.combat.model.Phase;
import norman.gurps.combat.model.SkillRollResult;
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
            combatant.setHealthStatus(HealthStatus.ALIVE);
            int basicMove = combatant.getGameChar().getBasicMove();
            int encumbranceLevel = combatant.getGameChar().getEncumbranceLevel();
            combatant.setCurrentMove(basicMove - encumbranceLevel);
            combatant.setAction(Action.DO_NOTHING);
        }

        NextStep nextStep = new NextStep();
        nextStep.setRound(1);
        nextStep.setIndex(0);
        nextStep.setPhase(Phase.BEGIN);
        battle.setNextStep(nextStep);
        battleService.updateBattle(battle, "Combat started.");
    }

    public NextStep nextStep(Phase phase, Action action, String targetLabel, String weaponName, String modeName,
            Integer rollToHit, Defense defense, String defendingItemName, Integer rollToDefend) {
        LOGGER.debug("Next step in combat.");
        Battle battle = battleService.getBattle();
        if (battle.getNextStep() == null) {
            throw new LoggingException(LOGGER, "Combat has not started.");
        }
        if (phase == null) {
            throw new LoggingException(LOGGER, "Combat phase may not be blank.");
        } else if (phase != battle.getNextStep().getPhase()) {
            throw new LoggingException(LOGGER,
                    "Invalid phase " + phase + ". Does not match phase " + battle.getNextStep().getPhase() +
                            " of current battle.");
        }

        int round = battle.getNextStep().getRound();
        int index = battle.getNextStep().getIndex();

        Combatant combatant = battle.getCombatants().get(index);
        Combatant target = null;
        if (combatant.getTargetLabel() != null) {
            target = findCombatantByLabel(combatant.getTargetLabel(), battle.getCombatants());
        }

        NextStep nextStep = null;
        switch (phase) {
            case BEGIN:
                nextStep = doBegin(round, index, combatant);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle, "Begin next turn for " + combatant.getLabel() + ".");
                break;
            case PROMPT_FOR_ACTION:
                nextStep = doPromptForAction(round, index, combatant);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle, "Combatant " + combatant.getLabel() + " must chose an action.");
                break;
            case RESOLVE_ACTION:
                if (action == null) {
                    throw new LoggingException(LOGGER, "Action may not be blank.");
                }
                nextStep = doResolveAction(round, index, combatant, action);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle,
                        "Combatant " + combatant.getLabel() + " has chosen an action of " + action + ".");
                break;
            case PROMPT_FOR_TO_HIT:
                nextStep = doPromptForToHit(round, index, combatant);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle,
                        "Combatant " + combatant.getLabel() + " must chose a target, weapon / mode, and roll to hit.");
                break;
            case RESOLVE_TO_HIT:
                // Validate target.
                if (targetLabel == null) {
                    throw new LoggingException(LOGGER, "Target name may not be blank.");
                } else if (targetLabel.equals(combatant.getLabel())) {
                    throw new LoggingException(LOGGER,
                            "Combatant " + combatant.getLabel() + " may not target him/herself.");
                } else {
                    if (findCombatantByLabel(targetLabel, battle.getCombatants()) == null) {
                        throw new LoggingException(LOGGER,
                                "Target " + targetLabel + " is not a combatant in the current battle.");
                    }
                }

                // Validate weapon / mode.
                if (weaponName == null) {
                    throw new LoggingException(LOGGER, "Weapon name may not be blank.");
                } else {
                    MeleeWeapon weapon = findWeaponByName(weaponName, combatant.getGameChar().getMeleeWeapons());
                    if (weapon == null) {
                        throw new LoggingException(LOGGER,
                                "Weapon " + weaponName + " is not ready weapon for combatant " + combatant.getLabel() +
                                        ".");
                    } else if (modeName == null) {
                        throw new LoggingException(LOGGER, "Weapon mode name may not be blank.");
                    } else {
                        if (findModeByName(modeName, weapon.getModes()) == null) {
                            throw new LoggingException(LOGGER,
                                    "Mode " + modeName + " is not a mode of weapon " + weapon.getName() +
                                            " for combatant " + combatant.getLabel() + ".");
                        }
                    }
                }

                // Validate roll.
                if (rollToHit == null) {
                    throw new LoggingException(LOGGER, "Value rolled may not be blank.");
                }

                nextStep = doResolveToHit(round, index, combatant, targetLabel, weaponName, modeName, rollToHit);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle,
                        "Combatant " + combatant.getLabel() + " has chosen to attack " + targetLabel + " with " +
                                weaponName + " / " + modeName + ", and rolled " + rollToHit + " to hit.");
                break;
            case PROMPT_FOR_TO_DEFEND:
                nextStep = doPromptForToDefend(round, index, combatant);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle, "Target " + combatant.getTargetLabel() +
                        " must chose a defense, item (if needed for defense), and roll to defend");
                break;
            case RESOLVE_TO_DEFEND:
                nextStep = doResolveToDefend(round, index, combatant, target, defense, defendingItemName, rollToDefend);
                battle.setNextStep(nextStep);
                String defendingItem;
                if (defense == Defense.DODGE || defense == Defense.NO_DEFENSE) {
                    defendingItem = "";
                } else {
                    defendingItem = " (" + defendingItemName + ")";
                }
                battleService.updateBattle(battle,
                        "Target " + targetLabel + " has chosen a defense of " + defense + defendingItem +
                                ", and rolled " + rollToDefend + " to defend");
                break;
            case PROMPT_FOR_DAMAGE:
                break;
            case RESOLVE_DAMAGE:
                break;
            case END:
                break;
        }
        return battle.getNextStep();
    }

    private NextStep doBegin(int round, int index, Combatant combatant) {
        // Calculate health status.
        int hitPoints = combatant.getGameChar().getHitPoints();
        int currentDamage = combatant.getCurrentDamage();
        int previousDamage = combatant.getPreviousDamage();
        int remainingHitPoints = hitPoints - currentDamage - previousDamage;
        HealthStatus status;
        if ((double) remainingHitPoints >= (double) hitPoints / 3.0) {
            status = HealthStatus.ALIVE;
        } else if (remainingHitPoints > 0) {
            status = HealthStatus.REELING;
        } else if (remainingHitPoints > -1 * hitPoints) {
            status = HealthStatus.BARELY;
        } else if (remainingHitPoints > -2 * hitPoints) {
            status = HealthStatus.ALMOST;
        } else if (remainingHitPoints > -3 * hitPoints) {
            status = HealthStatus.ALMOST2;
        } else if (remainingHitPoints > -4 * hitPoints) {
            status = HealthStatus.ALMOST3;
        } else if (remainingHitPoints > -5 * hitPoints) {
            status = HealthStatus.ALMOST3;
        } else if (remainingHitPoints > -10 * hitPoints) {
            status = HealthStatus.DEAD;
        } else {
            status = HealthStatus.DESTROYED;
        }

        // Calculate current move.
        int basicMove = combatant.getGameChar().getBasicMove();
        int encumbranceLevel = combatant.getGameChar().getEncumbranceLevel();
        int currentMove = 0;
        if (status == HealthStatus.ALIVE) {
            currentMove = basicMove - encumbranceLevel;
        } else if (status == HealthStatus.REELING) {
            currentMove = (int) Math.ceil(((double) basicMove - (double) encumbranceLevel) / 2.0);
        }

        // Calculate next phase.
        Phase phase = Phase.END;
        if (status == HealthStatus.ALIVE || status == HealthStatus.REELING || status == HealthStatus.BARELY) {
            phase = Phase.PROMPT_FOR_ACTION;
        }

        combatant.setCurrentDamage(0);
        combatant.setPreviousDamage(currentDamage + previousDamage);
        combatant.setHealthStatus(status);
        combatant.setCurrentMove(currentMove);

        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setPhase(phase);
        nextStep.setInputNeeded(false);
        nextStep.setMessage(
                "" + nextStep.getRound() + " / " + nextStep.getIndex() + " : " + combatant.getLabel() + " is " +
                        status + ".");
        return nextStep;
    }

    private NextStep doPromptForAction(int round, int index, Combatant combatant) {
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setPhase(Phase.RESOLVE_ACTION);
        nextStep.setInputNeeded(true);
        nextStep.setMessage("" + nextStep.getRound() + " / " + nextStep.getIndex() + " : " + combatant.getLabel() +
                ", please chose an action.");
        return nextStep;
    }

    private NextStep doResolveAction(int round, int index, Combatant combatant, Action action) {
        // Calculate next phase.
        combatant.setAction(action);

        Phase phase = Phase.END;
        if (action == Action.ATTACK) {
            phase = Phase.PROMPT_FOR_TO_HIT;
        }

        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setPhase(phase);
        nextStep.setInputNeeded(false);
        return nextStep;
    }

    private NextStep doPromptForToHit(int round, int index, Combatant combatant) {
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setPhase(Phase.RESOLVE_TO_HIT);
        nextStep.setInputNeeded(true);
        nextStep.setMessage("" + nextStep.getRound() + " / " + nextStep.getIndex() + " : " + combatant.getLabel() +
                ", please chose a target, a weapon / mode, and roll to hit.");
        return nextStep;
    }

    private NextStep doResolveToHit(int round, int index, Combatant combatant, String targetLabel, String weaponName,
            String modeName, int rollToHit) {
        MeleeWeapon weapon = findWeaponByName(weaponName, combatant.getGameChar().getMeleeWeapons());
        int skill = weapon.getSkill();
        if (weapon.getMinStrength() > combatant.getGameChar().getStrength()) {
            skill -= (weapon.getMinStrength() - combatant.getGameChar().getStrength());
        }

        int margin = skill - rollToHit;
        SkillRollResult result;
        if (rollToHit == 3) {
            result = SkillRollResult.CRITICAL_SUCCESS;
        } else if (rollToHit == 4) {
            result = SkillRollResult.CRITICAL_SUCCESS;
        } else if (rollToHit == 5 && skill >= 15) {
            result = SkillRollResult.CRITICAL_SUCCESS;
        } else if (rollToHit == 6 && skill >= 16) {
            result = SkillRollResult.CRITICAL_SUCCESS;
        } else if (rollToHit == 18) {
            result = SkillRollResult.CRITICAL_FAILURE;
        } else if (rollToHit == 17 && skill <= 15) {
            result = SkillRollResult.CRITICAL_FAILURE;
        } else if (margin <= -10) {
            result = SkillRollResult.CRITICAL_FAILURE;
        } else if (margin >= 0) {
            result = SkillRollResult.SUCCESS;
        } else {
            result = SkillRollResult.FAILURE;
        }

        combatant.setTargetLabel(targetLabel);
        combatant.setWeaponName(weaponName);
        combatant.setModeName(modeName);
        combatant.setEffectiveSkillToHit(skill);
        combatant.setRollToHit(rollToHit);
        combatant.setToHitResult(result);

        //todo Handle critical rolls.
        Phase phase;
        String message;
        if (result == SkillRollResult.CRITICAL_SUCCESS || result == SkillRollResult.SUCCESS) {
            phase = Phase.PROMPT_FOR_TO_DEFEND;
            message = combatant.getLabel() + " successfully attacked " + combatant.getTargetLabel() + " with " +
                    weaponName + " / " + modeName + ". Rolled a " + rollToHit + ", needed a " + skill + ".";
        } else {
            phase = Phase.END;
            message = combatant.getLabel() + " attacked " + combatant.getTargetLabel() + " with " + weaponName + " / " +
                    modeName + ", but failed to hit. Rolled a " + rollToHit + ", but needed a " + skill + ".";
        }

        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setPhase(phase);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + nextStep.getRound() + " / " + nextStep.getIndex() + " : " + message);
        return nextStep;
    }

    private NextStep doPromptForToDefend(int round, int index, Combatant combatant) {
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setPhase(Phase.RESOLVE_TO_HIT);
        nextStep.setInputNeeded(true);
        nextStep.setMessage(
                "" + nextStep.getRound() + " / " + nextStep.getIndex() + " : " + combatant.getTargetLabel() +
                        ", please chose a defense, an item (if needed for defense), and roll to defend");
        return nextStep;
    }

    private NextStep doResolveToDefend(int round, int index, Combatant combatant, Combatant target, Defense defense,
            String defendingItemName, Integer rollToDefend) {
        return null;
    }

    private Combatant findCombatantByLabel(String label, List<Combatant> combatants) {
        Combatant found = null;
        for (Combatant combatant : combatants) {
            if (label.equals(combatant.getLabel())) {
                found = combatant;
            }
        }
        return found;
    }

    private static MeleeWeapon findWeaponByName(String name, List<MeleeWeapon> weapons) {
        MeleeWeapon found = null;
        for (MeleeWeapon weapon : weapons) {
            if (name.equals(weapon.getName())) {
                found = weapon;
            }
        }
        return found;
    }

    private static MeleeWeaponMode findModeByName(String name, List<MeleeWeaponMode> modes) {
        MeleeWeaponMode found = null;
        for (MeleeWeaponMode mode : modes) {
            if (name.equals(mode.getName())) {
                found = mode;
            }
        }
        return found;
    }
}