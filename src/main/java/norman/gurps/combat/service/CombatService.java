package norman.gurps.combat.service;

import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.Action;
import norman.gurps.combat.model.ActiveDefense;
import norman.gurps.combat.model.Armor;
import norman.gurps.combat.model.Battle;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.DamageType;
import norman.gurps.combat.model.DefenseType;
import norman.gurps.combat.model.HealthStatus;
import norman.gurps.combat.model.Location;
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
            combatant.setNbrOfDeathChecksNeeded(0);
            combatant.setDeathCheckFailed(false);
            HealthStatus status = HealthStatus.ALIVE;
            combatant.setHealthStatus(status);
            int basicMove = combatant.getGameChar().getBasicMove();
            int encumbranceLevel = combatant.getGameChar().getEncumbranceLevel();
            int currentMove = getCurrentMove(status, basicMove, encumbranceLevel);
            combatant.setCurrentMove(currentMove);
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
            Integer rollToHit, DefenseType defenseType, String defendingItemName, Integer rollToDefend,
            Integer rollForDamage, Integer rollForDeathCheck) {
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
            target = getCombatant(combatant.getTargetLabel(), battle.getCombatants());
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
                validateResolveAction(action);
                nextStep = doResolveAction(round, index, combatant, action);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle,
                        "Combatant " + combatant.getLabel() + " has chosen an action of " + action + ".");
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
                validateResolveToHit(rollToHit);
                nextStep = doResolveToHit(round, index, combatant, target, rollToHit);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle,
                        "Combatant " + combatant.getLabel() + " has rolled " + rollToHit + " to hit.");
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
                validateResolveToDefend(rollToDefend);
                nextStep = doResolveToDefend(round, index, combatant, target, rollToDefend);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle,
                        "Target " + target.getLabel() + " has rolled " + rollToDefend + " to defend.");
                break;
            case PROMPT_FOR_DAMAGE:
                nextStep = doPromptForDamage(round, index, combatant);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle, "Combatant " + combatant.getLabel() + " must roll for damage.");
                break;
            case RESOLVE_DAMAGE:
                validateResolveDamage(rollForDamage);
                nextStep = doResolveDamage(round, index, combatant, target, rollForDamage);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle,
                        "Target " + target.getLabel() + " was hit with " + rollForDamage + ".");
                break;
            case PROMPT_FOR_DEATH_CHECK:
                nextStep = doPromptForDeathCheck(round, index, target);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle,
                        "Target " + target.getLabel() + " must make a health roll to avoid dying.");
                break;
            case RESOLVE_DEATH_CHECK:
                validateResolveDeathCheck(rollForDeathCheck);
                nextStep = doResolveDeathCheck(round, index, target, rollForDeathCheck);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle,
                        "Target " + target.getLabel() + " rolled " + rollForDeathCheck + " to avoid dying.");
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
        HealthStatus status = combatant.getHealthStatus();
        Phase phase;
        String message;
        if (status == HealthStatus.ALIVE || status == HealthStatus.REELING || status == HealthStatus.BARELY) {
            phase = Phase.PROMPT_FOR_ACTION;
            message = "It is now " + combatant.getLabel() + "'s turn. He/she is " + status + ".";
        } else {
            phase = Phase.END;
            message = combatant.getLabel() + " is " + status + ". His/her turn is being skipped.";
        }

        // Initialize combatant.
        combatant.setAction(null);
        combatant.setTargetLabel(null);
        combatant.setWeaponName(null);
        combatant.setModeName(null);
        combatant.setEffectiveSkillToHit(null);
        combatant.setRollToHit(null);
        combatant.setToHitResult(null);
        combatant.setDamageDice(null);
        combatant.setDamageAdds(null);
        combatant.setRollForDamage(null);
        combatant.getActiveDefenses().clear();

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setPhase(phase);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doPromptForAction(int round, int index, Combatant combatant) {
        String message = combatant.getLabel() + ", please chose an action.";

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setPhase(Phase.RESOLVE_ACTION);
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
        nextStep.setPhase(Phase.RESOLVE_TARGET_AND_WEAPON);
        nextStep.setInputNeeded(true);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doPromptForToHit(int round, int index, Combatant combatant) {
        String weaponName = combatant.getWeaponName();
        MeleeWeapon weapon = getWeapon(weaponName, combatant.getGameChar().getMeleeWeapons());
        int skill = weapon.getSkill();
        if (weapon.getMinStrength() > combatant.getGameChar().getStrength()) {
            skill -= (weapon.getMinStrength() - combatant.getGameChar().getStrength());
        }
        String message = combatant.getLabel() + ", please roll 3d (need " + skill + " to hit).";

        // Update combatant.
        combatant.setEffectiveSkillToHit(skill);

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setPhase(Phase.RESOLVE_TO_HIT);
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
        nextStep.setPhase(Phase.RESOLVE_DEFENSE);
        nextStep.setInputNeeded(true);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doPromptForToDefend(int round, int index, Combatant target) {
        int size = target.getActiveDefenses().size();
        ActiveDefense defense = target.getActiveDefenses().get(size - 1);
        int skill = 0;
        DefenseType type = defense.getDefenseType();
        if (type == DefenseType.PARRY) {
            MeleeWeapon weapon = getWeapon(defense.getDefendingItemName(), target.getGameChar().getMeleeWeapons());
            int weaponSkill = weapon.getSkill();
            if (weapon.getMinStrength() > target.getGameChar().getStrength()) {
                weaponSkill -= (weapon.getMinStrength() - target.getGameChar().getStrength());
            }
            skill = (weaponSkill / 2) + 3 + weapon.getParryModifier();
        } else if (type == DefenseType.BLOCK) {
            Shield shield = getShield(defense.getDefendingItemName(), target.getGameChar().getShields());
            int shieldSkill = shield.getSkill();
            skill = (shieldSkill / 2) + 3;
        } else if (type == DefenseType.DODGE) {
            skill = target.getCurrentMove() + 3;
        }
        if (!target.getGameChar().getShields().isEmpty()) {
            skill += target.getGameChar().getShields().get(0).getDefenseBonus();
        }
        String message = target.getLabel() + ", please roll 3d (need " + skill + " to defend).";

        // Update target.
        defense.setEffectiveSkillToDefend(skill);

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setPhase(Phase.RESOLVE_TO_DEFEND);
        nextStep.setInputNeeded(true);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doPromptForDamage(int round, int index, Combatant combatant) {
        MeleeWeapon weapon = getWeapon(combatant.getWeaponName(), combatant.getGameChar().getMeleeWeapons());
        MeleeWeaponMode mode = getWeaponMode(combatant.getModeName(), weapon.getModes());
        String dmgDesc = getDamageDescription(mode.getDamageDice(), mode.getDamageAdds(), mode.getDamageType());
        String message = combatant.getLabel() + ", please roll " + dmgDesc + " damage.";

        // Update combatant.
        combatant.setDamageDice(mode.getDamageDice());
        combatant.setDamageAdds(mode.getDamageAdds());

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setPhase(Phase.RESOLVE_DAMAGE);
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
        nextStep.setPhase(Phase.RESOLVE_DEATH_CHECK);
        nextStep.setInputNeeded(true);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void validateResolveAction(Action action) {
        // Validate action.
        if (action == null) {
            throw new LoggingException(LOGGER, "Action may not be blank.");
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
            MeleeWeapon weapon = getWeapon(weaponName, combatant.getGameChar().getMeleeWeapons());
            if (weapon == null) {
                throw new LoggingException(LOGGER,
                        "Weapon " + weaponName + " is not a ready weapon for combatant " + combatant.getLabel() + ".");
            } else if (modeName == null) {
                throw new LoggingException(LOGGER, "Weapon mode may not be blank.");
            } else {
                if (getWeaponMode(modeName, weapon.getModes()) == null) {
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
            if (getWeapon(defendingItemName, target.getGameChar().getMeleeWeapons()) == null) {
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

    private NextStep doResolveAction(int round, int index, Combatant combatant, Action action) {
        Phase phase;
        if (action == Action.ATTACK) {
            phase = Phase.PROMPT_FOR_TARGET_AND_WEAPON;
        } else {
            phase = Phase.END;
        }
        String message = combatant.getLabel() + " has chosen action " + action + ".";

        // Update combatant.
        combatant.setAction(action);

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setPhase(phase);
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
        nextStep.setPhase(Phase.PROMPT_FOR_TO_HIT);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doResolveToHit(int round, int index, Combatant combatant, Combatant target, int rollToHit) {
        int skill = combatant.getEffectiveSkillToHit();
        SkillRollResult result = getSkillRollResult(skill, rollToHit);
        String weaponName = combatant.getWeaponName();
        String modeName = combatant.getModeName();
        Phase phase;
        String message;
        if (result == SkillRollResult.CRITICAL_SUCCESS || result == SkillRollResult.SUCCESS) {
            phase = Phase.PROMPT_FOR_DEFENSE;
            message =
                    combatant.getLabel() + " successfully attacked " + target.getLabel() + " with " + weaponName + "/" +
                            modeName + ". Rolled a " + rollToHit + ", needed a " + skill + ".";
        } else {
            phase = Phase.END;
            message = combatant.getLabel() + " attacked " + target.getLabel() + " with " + weaponName + "/" + modeName +
                    ", but failed to hit. Rolled a " + rollToHit + ", but needed a " + skill + ".";
        }

        // Update combatant.
        combatant.setRollToHit(rollToHit);
        combatant.setToHitResult(result);

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setPhase(phase);
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
        ActiveDefense defense = new ActiveDefense();
        defense.setDefenseType(type);
        defense.setDefendingItemName(item);
        target.getActiveDefenses().add(defense);

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setPhase(Phase.PROMPT_FOR_TO_DEFEND);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doResolveToDefend(int round, int index, Combatant combatant, Combatant target,
            Integer rollToDefend) {
        int size = target.getActiveDefenses().size();
        ActiveDefense defense = target.getActiveDefenses().get(size - 1);
        int skill = defense.getEffectiveSkillToDefend();
        SkillRollResult result = getSkillRollResult(skill, rollToDefend);
        DefenseType type = defense.getDefenseType();
        String item = defense.getDefendingItemName();
        String itemParenth = "";
        if (type != DefenseType.DODGE && type != DefenseType.NO_DEFENSE) {
            itemParenth = " (" + item + ")";
        }
        Phase phase;
        String message;
        if (result == SkillRollResult.CRITICAL_SUCCESS || result == SkillRollResult.SUCCESS) {
            phase = Phase.END;
            message = target.getLabel() + " successfully defended against " + combatant.getLabel() + " with " + type +
                    itemParenth + ". Rolled a " + rollToDefend + ", needed a " + skill + ".";
        } else {
            phase = Phase.PROMPT_FOR_DAMAGE;
            message = target.getLabel() + " failed to defend against " + combatant.getLabel() + " with " + type +
                    itemParenth + ". Rolled a " + rollToDefend + ", but needed a " + skill + ".";
        }

        // Update target.
        defense.setRollToDefend(rollToDefend);
        defense.setToDefendResult(result);

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setPhase(phase);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doResolveDamage(int round, int index, Combatant combatant, Combatant target,
            Integer rollForDamage) {
        Armor armor = getArmor(Location.TORSO, target.getGameChar().getArmorList());
        int dr = armor.getDamageResistance();
        int penetratingDamage = rollForDamage - dr;
        MeleeWeapon weapon = getWeapon(combatant.getWeaponName(), combatant.getGameChar().getMeleeWeapons());
        MeleeWeaponMode mode = getWeaponMode(combatant.getModeName(), weapon.getModes());
        DamageType type = mode.getDamageType();
        double mult = getDamageMultiplier(type);
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
        Phase phase = Phase.END;
        if (needed > 0) {
            phase = Phase.PROMPT_FOR_DEATH_CHECK;
        }
        String message =
                target.getLabel() + " is hit for " + rollForDamage + " " + type + " damage. " + penetratingDamage +
                        " gets through his/her armour (DR " + dr + ") doing " + injuryDamage + " hits of damage.";

        // Update combatant.
        combatant.setRollForDamage(rollForDamage);

        // Update target.
        target.setCurrentDamage(newCurrentDamage);
        target.setNbrOfDeathChecksNeeded(needed);
        target.setHealthStatus(newStatus);
        target.setCurrentMove(newCurrentMove);

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setPhase(phase);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doResolveDeathCheck(int round, int index, Combatant target, Integer rollForDeathCheck) {
        //        int nbrOfDeathChecksNeeded = target.getNbrOfDeathChecksNeeded();
        //        boolean deathCheckFailed;
        //        HealthStatus status;
        //        String message;
        //        if (result == SkillRollResult.FAILURE || result == SkillRollResult.CRITICAL_FAILURE) {
        //            status = HealthStatus.DEAD;
        //            succeeded = false;
        //        } else {
        //            succeeded = true;
        //            status = target.getHealthStatus();
        //        }

        int check = target.getGameChar().getDeathCheck();
        SkillRollResult result = getSkillRollResult(check, rollForDeathCheck);
        int nbr = target.getNbrOfDeathChecksNeeded();
        boolean failed = false;
        HealthStatus status = target.getHealthStatus();
        int move = target.getCurrentMove();
        Phase phase = Phase.END;
        String message;
        if (result == SkillRollResult.CRITICAL_SUCCESS || result == SkillRollResult.SUCCESS) {
            nbr--;
            if (nbr > 0) {
                phase = Phase.PROMPT_FOR_DEATH_CHECK;
            }
            message = target.getLabel() + " successfully avoided death! Rolled a " + rollForDeathCheck + ", needed a " +
                    check + ").";
        } else {
            nbr = 0;
            failed = true;
            status = HealthStatus.DEAD;
            move = 0;
            message = target.getLabel() + " is now dead! Rolled a" + rollForDeathCheck + ", needed a " + check + ").";
        }

        // Update target.
        target.setNbrOfDeathChecksNeeded(nbr);
        target.setDeathCheckFailed(failed);
        target.setHealthStatus(status);
        target.setCurrentMove(move);

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setPhase(phase);
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
        nextStep.setPhase(Phase.BEGIN);
        nextStep.setInputNeeded(false);
        return nextStep;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private Armor getArmor(Location location, List<Armor> armorList) {
        Armor found = null;
        for (Armor armor : armorList) {
            if (armor.getLocation() == location) {
                found = armor;
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

    private double getDamageMultiplier(DamageType type) {
        double multiplier = 1.0;
        if (type == DamageType.SMALL_PIERCING) {
            multiplier = 0.5;
        } else if (type == DamageType.CUTTING || type == DamageType.LARGE_PIERCING) {
            multiplier = 1.5;
        } else if (type == DamageType.IMPALING || type == DamageType.HUGE_PIERCING) {
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
        HealthStatus status = null;
        if (level == 1) {
            status = HealthStatus.ALIVE;
        } else if (level == 2) {
            status = HealthStatus.REELING;
        } else if (level == 3) {
            status = deathCheckFailed ? HealthStatus.UNCONSCIOUS : HealthStatus.BARELY;
        } else if (level == 4) {
            status = deathCheckFailed ? HealthStatus.DEAD : HealthStatus.ALMOST;
        } else if (level == 5) {
            status = deathCheckFailed ? HealthStatus.DEAD : HealthStatus.ALMOST2;
        } else if (level == 6) {
            status = deathCheckFailed ? HealthStatus.DEAD : HealthStatus.ALMOST3;
        } else if (level == 7) {
            status = deathCheckFailed ? HealthStatus.DEAD : HealthStatus.ALMOST4;
        } else if (level == 8) {
            status = HealthStatus.DEAD;
        } else if (level == 9) {
            status = HealthStatus.DESTROYED;
        }
        return status;
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

    private SkillRollResult getSkillRollResult(int skill, int roll) {
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

    private MeleeWeapon getWeapon(String name, List<MeleeWeapon> weapons) {
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