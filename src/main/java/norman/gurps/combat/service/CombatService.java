package norman.gurps.combat.service;

import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.Action;
import norman.gurps.combat.model.Battle;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.DamageType;
import norman.gurps.combat.model.Defense;
import norman.gurps.combat.model.HealthStatus;
import norman.gurps.combat.model.MeleeWeapon;
import norman.gurps.combat.model.MeleeWeaponMode;
import norman.gurps.combat.model.NextStep;
import norman.gurps.combat.model.Phase;
import norman.gurps.combat.model.Shield;
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
                        "Combatant " + combatant.getLabel() + " must chose a target, weapon & mode, and roll to hit.");
                break;
            case RESOLVE_TO_HIT:
                // Validate target.
                if (targetLabel == null) {
                    throw new LoggingException(LOGGER, "Target may not be blank.");
                } else if (targetLabel.equals(combatant.getLabel())) {
                    throw new LoggingException(LOGGER,
                            "Combatant " + combatant.getLabel() + " may not target him/herself.");
                } else {
                    if (findCombatantByLabel(targetLabel, battle.getCombatants()) == null) {
                        throw new LoggingException(LOGGER,
                                "Target " + targetLabel + " is not a combatant in the current battle.");
                    }
                }

                // Validate weapon & mode.
                if (weaponName == null) {
                    throw new LoggingException(LOGGER, "Weapon may not be blank.");
                } else {
                    MeleeWeapon weapon = findWeaponByName(weaponName, combatant.getGameChar().getMeleeWeapons());
                    if (weapon == null) {
                        throw new LoggingException(LOGGER,
                                "Weapon " + weaponName + " is not a ready weapon for combatant " +
                                        combatant.getLabel() + ".");
                    } else if (modeName == null) {
                        throw new LoggingException(LOGGER, "Weapon mode may not be blank.");
                    } else {
                        if (findModeByName(modeName, weapon.getModes()) == null) {
                            throw new LoggingException(LOGGER,
                                    "Mode " + modeName + " is not a valid mode of weapon " + weapon.getName() +
                                            " for combatant " + combatant.getLabel() + ".");
                        }
                    }
                }

                // Validate roll.
                if (rollToHit == null) {
                    throw new LoggingException(LOGGER, "Value rolled to hit may not be blank.");
                }

                nextStep = doResolveToHit(round, index, combatant, targetLabel, weaponName, modeName, rollToHit);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle,
                        "Combatant " + combatant.getLabel() + " has chosen to attack " + targetLabel + " with " +
                                weaponName + " & " + modeName + ", and rolled " + rollToHit + " to hit.");
                break;
            case PROMPT_FOR_TO_DEFEND:
                nextStep = doPromptForToDefend(round, index, target);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle, "Target " + target.getLabel() +
                        " must chose a defense, item (if needed for defense), and roll to defend.");
                break;
            case RESOLVE_TO_DEFEND:
                // Validate defense.
                if (defense == null) {
                    throw new LoggingException(LOGGER, "Defense may not be blank.");
                }

                // Validate defending item name.
                if ((defense == Defense.PARRY || defense == Defense.BLOCK) && defendingItemName == null) {
                    throw new LoggingException(LOGGER,
                            "If Defense is PARRY or BLOCK, defending item name may not be blank.");
                }

                // Validate weapon for parry.
                if (defense == Defense.PARRY) {
                    if (findWeaponByName(defendingItemName, target.getGameChar().getMeleeWeapons()) == null) {
                        throw new LoggingException(LOGGER,
                                "Weapon " + defendingItemName + " is not a ready parrying weapon for combatant " +
                                        target.getLabel() + ".");
                    }
                }

                // Validate shield for block.
                if (defense == Defense.BLOCK) {
                    if (findShieldByName(defendingItemName, target.getGameChar().getShields()) == null) {
                        throw new LoggingException(LOGGER,
                                "Shield " + defendingItemName + " is not a ready shield for combatant " +
                                        target.getLabel() + ".");
                    }
                }

                // Validate roll.
                if (rollToDefend == null) {
                    throw new LoggingException(LOGGER, "Value rolled to defend may not be blank.");
                }

                nextStep = doResolveToDefend(round, index, combatant, target, defense, defendingItemName, rollToDefend);
                battle.setNextStep(nextStep);
                String item;
                if (defense == Defense.DODGE || defense == Defense.NO_DEFENSE) {
                    item = "";
                } else {
                    item = " (" + defendingItemName + ")";
                }
                battleService.updateBattle(battle,
                        "Target " + target.getLabel() + " has chosen a defense of " + defense + item + ", and rolled " +
                                rollToDefend + " to defend.");
                break;
            case PROMPT_FOR_DAMAGE:
                nextStep = doPromptForDamage(round, index, combatant);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle, "Combatant " + combatant.getLabel() + " must roll for damage.");
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
        HealthStatus status = calculateHealthStatus(hitPoints, remainingHitPoints);

        // Calculate current move.
        int basicMove = combatant.getGameChar().getBasicMove();
        int encumbranceLevel = combatant.getGameChar().getEncumbranceLevel();
        int currentMove = 0;
        if (status == HealthStatus.ALIVE) {
            currentMove = basicMove - encumbranceLevel;
        } else if (status == HealthStatus.REELING) {
            currentMove = (int) Math.ceil(((double) basicMove - (double) encumbranceLevel) / 2.0);
        }

        combatant.setCurrentDamage(0);
        combatant.setPreviousDamage(currentDamage + previousDamage);
        combatant.setHealthStatus(status);
        combatant.setCurrentMove(currentMove);
        combatant.setAction(null);
        combatant.setTargetLabel(null);
        combatant.setWeaponName(null);
        combatant.setModeName(null);
        combatant.setEffectiveSkillToHit(null);
        combatant.setRollToHit(null);
        combatant.setToHitResult(null);

        Phase phase = Phase.END;
        if (status == HealthStatus.ALIVE || status == HealthStatus.REELING || status == HealthStatus.BARELY) {
            phase = Phase.PROMPT_FOR_ACTION;
        }

        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setPhase(phase);
        nextStep.setInputNeeded(false);
        String message = combatant.getLabel() + " is " + status + ".";
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doPromptForAction(int round, int index, Combatant combatant) {
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setPhase(Phase.RESOLVE_ACTION);
        nextStep.setInputNeeded(true);
        String message = combatant.getLabel() + ", please chose an action.";
        nextStep.setMessage("" + round + "/" + index + " : " + message);
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
        String message = combatant.getLabel() + ", please chose a target, a weapon & mode, and roll to hit.";
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doResolveToHit(int round, int index, Combatant combatant, String targetLabel, String weaponName,
            String modeName, int rollToHit) {
        MeleeWeapon weapon = findWeaponByName(weaponName, combatant.getGameChar().getMeleeWeapons());
        int weaponSkill = weapon.getSkill();
        if (weapon.getMinStrength() > combatant.getGameChar().getStrength()) {
            weaponSkill -= (weapon.getMinStrength() - combatant.getGameChar().getStrength());
        }

        SkillRollResult result = calculateSkillRollResult(weaponSkill, rollToHit);

        combatant.setTargetLabel(targetLabel);
        combatant.setWeaponName(weaponName);
        combatant.setModeName(modeName);
        combatant.setEffectiveSkillToHit(weaponSkill);
        combatant.setRollToHit(rollToHit);
        combatant.setToHitResult(result);

        //todo Handle critical rolls.
        Phase phase;
        String message;
        if (result == SkillRollResult.CRITICAL_SUCCESS || result == SkillRollResult.SUCCESS) {
            phase = Phase.PROMPT_FOR_TO_DEFEND;
            message = combatant.getLabel() + " successfully attacked " + targetLabel + " with " + weaponName + " & " +
                    modeName + ". Rolled a " + rollToHit + ", needed a " + weaponSkill + ".";
        } else {
            phase = Phase.END;
            message = combatant.getLabel() + " attacked " + targetLabel + " with " + weaponName + " & " + modeName +
                    ", but failed to hit. Rolled a " + rollToHit + ", but needed a " + weaponSkill + ".";
        }

        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setPhase(phase);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doPromptForToDefend(int round, int index, Combatant target) {
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setPhase(Phase.RESOLVE_TO_HIT);
        nextStep.setInputNeeded(true);
        String message =
                target.getLabel() + ", please chose a defense, an item (if needed for defense), and roll to defend.";
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doResolveToDefend(int round, int index, Combatant combatant, Combatant target, Defense defense,
            String defendingItemName, Integer rollToDefend) {
        int defenseSkill = 0;
        if (defense == Defense.PARRY) {
            MeleeWeapon weapon = findWeaponByName(defendingItemName, target.getGameChar().getMeleeWeapons());
            int weaponSkill = weapon.getSkill();
            if (weapon.getMinStrength() > target.getGameChar().getStrength()) {
                weaponSkill -= (weapon.getMinStrength() - target.getGameChar().getStrength());
            }
            defenseSkill = (weaponSkill / 2) + 3 + weapon.getParryModifier();
        } else if (defense == Defense.BLOCK) {
            Shield shield = findShieldByName(defendingItemName, target.getGameChar().getShields());
            int shieldSkill = shield.getSkill();
            defenseSkill = (shieldSkill / 2) + 3;
        } else if (defense == Defense.DODGE) {
            defenseSkill = target.getCurrentMove() + 3;
        }

        //todo Defense bonus should be in combatant object.
        if (!target.getGameChar().getShields().isEmpty()) {
            defenseSkill += target.getGameChar().getShields().get(0).getDefenseBonus();
        }

        SkillRollResult result = calculateSkillRollResult(defenseSkill, rollToDefend);

        String item;
        if (defense == Defense.DODGE || defense == Defense.NO_DEFENSE) {
            item = "";
        } else {
            item = " (" + defendingItemName + ")";
        }
        Phase phase;
        String message;
        if (result == SkillRollResult.CRITICAL_SUCCESS || result == SkillRollResult.SUCCESS) {
            phase = Phase.END;
            message =
                    target.getLabel() + " successfully defended against " + combatant.getLabel() + " with " + defense +
                            item + ". Rolled a " + rollToDefend + ", needed a " + defenseSkill + ".";
        } else {
            phase = Phase.PROMPT_FOR_DAMAGE;
            message = target.getLabel() + " failed to defend against " + combatant.getLabel() + " with " + defense +
                    item + ". Rolled a " + rollToDefend + ", but needed a " + defenseSkill + ".";
        }

        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setPhase(phase);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doPromptForDamage(int round, int index, Combatant combatant) {
        MeleeWeapon weapon = findWeaponByName(combatant.getWeaponName(), combatant.getGameChar().getMeleeWeapons());
        MeleeWeaponMode mode = findModeByName(combatant.getModeName(), weapon.getModes());
        String damageDescription = calculateDamageDescription(mode.getDamageDice(), mode.getDamageAdds(),
                mode.getDamageType());

        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setPhase(Phase.RESOLVE_DAMAGE);
        nextStep.setInputNeeded(true);
        String message = combatant.getLabel() + ", please roll " + damageDescription + " for damage.";
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private String calculateDamageDescription(int damageDice, int damageAdds, DamageType damageType) {
        StringBuilder sb = null;
        if (damageDice != 0) {
            sb = new StringBuilder(damageDice);
            sb.append("d");
        }
        if (damageAdds != 0) {
            if (sb == null) {
                sb = new StringBuilder(damageAdds);
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

    private HealthStatus calculateHealthStatus(int hitPoints, int remainingHitPoints) {
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
        return status;
    }

    private SkillRollResult calculateSkillRollResult(int skill, int roll) {
        int margin = skill - roll;
        SkillRollResult result;
        if (roll == 3) {
            result = SkillRollResult.CRITICAL_SUCCESS;
        } else if (roll == 4) {
            result = SkillRollResult.CRITICAL_SUCCESS;
        } else if (roll == 5 && skill >= 15) {
            result = SkillRollResult.CRITICAL_SUCCESS;
        } else if (roll == 6 && skill >= 16) {
            result = SkillRollResult.CRITICAL_SUCCESS;
        } else if (roll == 18) {
            result = SkillRollResult.CRITICAL_FAILURE;
        } else if (roll == 17 && skill <= 15) {
            result = SkillRollResult.CRITICAL_FAILURE;
        } else if (margin <= -10) {
            result = SkillRollResult.CRITICAL_FAILURE;
        } else if (margin >= 0) {
            result = SkillRollResult.SUCCESS;
        } else {
            result = SkillRollResult.FAILURE;
        }
        return result;
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

    private MeleeWeaponMode findModeByName(String name, List<MeleeWeaponMode> modes) {
        MeleeWeaponMode found = null;
        for (MeleeWeaponMode mode : modes) {
            if (name.equals(mode.getName())) {
                found = mode;
            }
        }
        return found;
    }

    private Shield findShieldByName(String name, List<Shield> shields) {
        Shield found = null;
        for (Shield shield : shields) {
            if (name.equals(shield.getName())) {
                found = shield;
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
}