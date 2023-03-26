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
            combatant.setUnconsciousnessCheckFailed(false);
            combatant.setNbrOfDeathChecksNeeded(0);
            combatant.setDeathCheckFailed(false);
            HealthStatus healthStatus = HealthStatus.ALIVE;
            combatant.setHealthStatus(healthStatus);
            int basicMove = combatant.getGameChar().getBasicMove();
            int encumbranceLevel = combatant.getGameChar().getEncumbranceLevel();
            int currentMove = getCurrentMove(healthStatus, basicMove, encumbranceLevel);
            combatant.setCurrentMove(currentMove);
            int defenseBonus = 0;
            if (!combatant.getGameChar().getShields().isEmpty()) {
                defenseBonus = combatant.getGameChar().getShields().get(0).getDefenseBonus();
            }
            combatant.setDefenseBonus(defenseBonus);
            combatant.setShockPenalty(0);
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
            String weaponModeName, Integer toHitRoll, DefenseType defenseType, String defendingItemName,
            Integer toDefendRoll, Integer forDamageRoll, Integer forDeathCheckRoll,
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
                validateResolveTargetAndWeapon(battle, combatant, targetLabel, weaponName, weaponModeName);
                nextStep = doResolveTargetAndWeapon(round, index, combatant, targetLabel, weaponName, weaponModeName);
                battle.setNextStep(nextStep);
                battleService.updateBattle(battle,
                        "Combatant " + combatant.getLabel() + " has chosen to attack " + targetLabel + " with " +
                                weaponName + "/" + weaponModeName + ".");
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
        int hitPoints = combatant.getGameChar().getHitPoints();
        int basicMove = combatant.getGameChar().getBasicMove();
        int encumbranceLevel = combatant.getGameChar().getEncumbranceLevel();
        int currentDamage = combatant.getCurrentDamage();
        int previousDamage = combatant.getPreviousDamage();
        int remainingHitPoints = hitPoints - currentDamage - previousDamage;
        boolean unconsciousnessCheckFailed = combatant.getUnconsciousnessCheckFailed();
        Boolean deathCheckFailed = combatant.getDeathCheckFailed();
        int defenseBonus = combatant.getDefenseBonus();

        int hitLevel = getHitLevel(hitPoints, remainingHitPoints);
        HealthStatus healthStatus = getHealthStatus(hitLevel, unconsciousnessCheckFailed, deathCheckFailed);
        int currentMove = getCurrentMove(healthStatus, basicMove, encumbranceLevel);
        int shockPenalty = getShockPenalty(currentDamage);

        combatant.setCurrentDamage(0);
        combatant.setPreviousDamage(currentDamage + previousDamage);
        combatant.setHealthStatus(healthStatus);
        combatant.setCurrentMove(currentMove);
        combatant.setShockPenalty(shockPenalty);

        int dodge = currentMove + 3 + defenseBonus;

        CombatPhase combatPhase;
        String message;
        if (healthStatus == HealthStatus.ALIVE) {
            combatPhase = CombatPhase.PROMPT_FOR_ACTION;
            message = "It is now " + combatant.getLabel() + "'s turn. He/she is " + healthStatus + ".";
            if (shockPenalty < 0) {
                message += " He/she is temporarily at " + shockPenalty +
                        " to all DX and IQ based skilled because of shock.";
            }
        } else if (healthStatus == HealthStatus.REELING) {
            combatPhase = CombatPhase.PROMPT_FOR_ACTION;
            message = "It is now " + combatant.getLabel() + "'s turn. He/she is " + healthStatus +
                    ". His/her current move is reduced to " + currentMove + " and dodge is reduced " + dodge + ".";
            if (shockPenalty < 0) {
                message += " He/she is temporarily at " + shockPenalty +
                        " to all DX and IQ based skilled because of shock.";
            }
        } else if (healthStatus == HealthStatus.BARELY || healthStatus == HealthStatus.ALMOST ||
                healthStatus == HealthStatus.ALMOST2 || healthStatus == HealthStatus.ALMOST3 ||
                healthStatus == HealthStatus.ALMOST4) {
            combatPhase = CombatPhase.PROMPT_FOR_UNCONSCIOUSNESS_CHECK;
            message = "It is now " + combatant.getLabel() + "'s turn. He/she is " + healthStatus +
                    ". His/her current move is reduced to " + currentMove + " and dodge is reduced " + dodge + ".";
            if (shockPenalty < 0) {
                message += " He/she is temporarily at " + shockPenalty +
                        " to all DX and IQ based skilled because of shock.";
            }
        } else {
            combatPhase = CombatPhase.END;
            message = combatant.getLabel() + " is " + healthStatus + ". His/her turn is being skipped.";
        }

        // Initialize combatant.
        combatant.setActionType(null);
        combatant.setTargetLabel(null);
        combatant.setWeaponName(null);
        combatant.setWeaponModeName(null);
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
        int unconsciousnessCheck = combatant.getGameChar().getUnconsciousnessCheck();
        String message = combatant.getLabel() + " needs to make a roll to stay conscious. Please roll 3d (need " +
                unconsciousnessCheck + ").";

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
        MeleeWeapon weapon = getWeapon(weaponName, combatant.getGameChar().getMeleeWeapons());
        int weaponSkill = weapon.getSkill() + combatant.getShockPenalty();
        if (weapon.getMinStrength() > combatant.getGameChar().getStrength()) {
            weaponSkill -= (weapon.getMinStrength() - combatant.getGameChar().getStrength());
        }
        String message = combatant.getLabel() + ", please roll 3d (need " + weaponSkill + " to hit).";

        // Update combatant.
        combatant.setToHitEffectiveSkill(weaponSkill);

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
        int defenseSkill = 0;
        DefenseType defenseType = combatDefense.getDefenseType();
        if (defenseType == DefenseType.PARRY) {
            MeleeWeapon weapon = getWeapon(combatDefense.getDefendingItemName(),
                    target.getGameChar().getMeleeWeapons());
            int weaponSkill = weapon.getSkill();
            if (weapon.getMinStrength() > target.getGameChar().getStrength()) {
                weaponSkill -= (weapon.getMinStrength() - target.getGameChar().getStrength());
            }
            defenseSkill = (weaponSkill / 2) + 3 + weapon.getParryModifier() + target.getDefenseBonus();
        } else if (defenseType == DefenseType.BLOCK) {
            Shield shield = getShield(combatDefense.getDefendingItemName(), target.getGameChar().getShields());
            int shieldSkill = shield.getSkill();
            defenseSkill = (shieldSkill / 2) + 3 + target.getDefenseBonus();
        } else if (defenseType == DefenseType.DODGE) {
            defenseSkill = target.getCurrentMove() + 3 + target.getDefenseBonus();
        }
        String message = target.getLabel() + ", please roll 3d (need " + defenseSkill + " to defend).";

        // Update target.
        combatDefense.setToDefendEffectiveSkill(defenseSkill);

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
        MeleeWeapon weapon = getWeapon(combatant.getWeaponName(), combatant.getGameChar().getMeleeWeapons());
        MeleeWeaponMode weaponMode = getWeaponMode(combatant.getWeaponModeName(), weapon.getMeleeWeaponModes());
        String damageDescription = getDamageDescription(weaponMode.getDamageDice(), weaponMode.getDamageAdds(),
                weaponMode.getDamageType());
        String message = combatant.getLabel() + ", please roll " + damageDescription + " damage.";

        // Update combatant.
        combatant.setDamageDice(weaponMode.getDamageDice());
        combatant.setDamageAdds(weaponMode.getDamageAdds());

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
        int deathCheck = target.getGameChar().getDeathCheck();
        int nbrOfDeathChecksNeeded = target.getNbrOfDeathChecksNeeded();
        String message;
        if (nbrOfDeathChecksNeeded > 1) {
            message = target.getLabel() + " needs to make " + nbrOfDeathChecksNeeded +
                    " rolls to avoid death. Please roll 3d (need " + deathCheck + ").";
        } else {
            message = target.getLabel() + " needs to make a roll to avoid death. Please roll 3d (need " + deathCheck +
                    ").";
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

    private void validateResolveUnconsciousnessCheck(Integer forUnconsciousnessCheckRoll) {
        // Validate roll.
        if (forUnconsciousnessCheckRoll == null) {
            throw new LoggingException(LOGGER, "Value rolled to remain conscious may not be blank.");
        }
    }

    private void validateResolveActionType(ActionType actionType) {
        // Validate action.
        if (actionType == null) {
            throw new LoggingException(LOGGER, "Action may not be blank.");
        }
    }

    private void validateResolveTargetAndWeapon(Battle battle, Combatant combatant, String targetLabel,
            String weaponName, String weaponModeName) {
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
            } else if (weaponModeName == null) {
                throw new LoggingException(LOGGER, "Weapon mode may not be blank.");
            } else {
                if (getWeaponMode(weaponModeName, weapon.getMeleeWeaponModes()) == null) {
                    throw new LoggingException(LOGGER,
                            "Mode " + weaponModeName + " is not a valid mode of weapon " + weapon.getName() +
                                    " for combatant " + combatant.getLabel() + ".");
                }
            }
        }
    }

    private void validateResolveToHit(Integer toHitRoll) {
        // Validate roll.
        if (toHitRoll == null) {
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
                        "If Defense is parry or block, defending item name may not be blank.");
            }
        } else {
            if (defendingItemName != null) {
                throw new LoggingException(LOGGER,
                        "If Defense is not parry and not block, defending item name must not be blank.");
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

    private void validateResolveToDefend(Integer toDefendRoll) {
        // Validate roll.
        if (toDefendRoll == null) {
            throw new LoggingException(LOGGER, "Value rolled to defend may not be blank.");
        }
    }

    private void validateResolveDamage(Integer forDamageRoll) {
        // Validate roll.
        if (forDamageRoll == null) {
            throw new LoggingException(LOGGER, "Value rolled for damage may not be blank.");
        } else if (forDamageRoll < 0) {
            throw new LoggingException(LOGGER, "Value rolled for damage may not be less than zero.");
        }
    }

    private void validateResolveDeathCheck(Integer forDeathCheckRoll) {
        // Validate roll.
        if (forDeathCheckRoll == null) {
            throw new LoggingException(LOGGER, "Value rolled to avoid death may not be blank.");
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private NextStep doResolveUnconsciousnessCheck(int round, int index, Combatant combatant,
            int forUnconsciousnessCheckRoll) {
        int unconsciousnessCheck = combatant.getGameChar().getUnconsciousnessCheck();
        ResultType resultType = getResultType(unconsciousnessCheck, forUnconsciousnessCheckRoll);

        boolean unconsciousnessCheckFailed = false;
        HealthStatus healthStatus = combatant.getHealthStatus();
        int currentMove = combatant.getCurrentMove();
        CombatPhase combatPhase;
        String message;
        if (resultType == ResultType.CRITICAL_SUCCESS || resultType == ResultType.SUCCESS) {
            combatPhase = CombatPhase.PROMPT_FOR_ACTION;
            message =
                    combatant.getLabel() + " successfully remained conscious! Rolled a " + forUnconsciousnessCheckRoll +
                            ", needed a " + unconsciousnessCheck + ".";
        } else {
            unconsciousnessCheckFailed = true;
            healthStatus = HealthStatus.UNCONSCIOUS;
            currentMove = 0;
            combatPhase = CombatPhase.END;
            message = combatant.getLabel() + " is now unconscious! Rolled a " + forUnconsciousnessCheckRoll +
                    ", needed a " + unconsciousnessCheck + ".";
        }

        // Update combatant.
        combatant.setUnconsciousnessCheckFailed(unconsciousnessCheckFailed);
        combatant.setHealthStatus(healthStatus);
        combatant.setCurrentMove(currentMove);

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(combatPhase);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doResolveActionType(int round, int index, Combatant combatant, ActionType actionType) {
        CombatPhase combatPhase;
        if (actionType == ActionType.ATTACK) {
            combatPhase = CombatPhase.PROMPT_FOR_TARGET_AND_WEAPON;
        } else {
            combatPhase = CombatPhase.END;
        }
        String message = combatant.getLabel() + " has chosen action " + actionType + ".";

        // Update combatant.
        combatant.setActionType(actionType);

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(combatPhase);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doResolveTargetAndWeapon(int round, int index, Combatant combatant, String targetLabel,
            String weaponName, String weaponModeName) {
        String message = combatant.getLabel() + " has chosen to attack " + targetLabel + " with " + weaponName + "/" +
                weaponModeName + ".";

        // Update combatant.
        combatant.setTargetLabel(targetLabel);
        combatant.setWeaponName(weaponName);
        combatant.setWeaponModeName(weaponModeName);

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
        ResultType resultType = getResultType(skill, toHitRoll);
        String weaponName = combatant.getWeaponName();
        String weaponModeName = combatant.getWeaponModeName();
        CombatPhase combatPhase;
        String message;
        if (resultType == ResultType.CRITICAL_SUCCESS || resultType == ResultType.SUCCESS) {
            combatPhase = CombatPhase.PROMPT_FOR_DEFENSE;
            message =
                    combatant.getLabel() + " successfully attacked " + target.getLabel() + " with " + weaponName + "/" +
                            weaponModeName + ". Rolled a " + toHitRoll + ", needed a " + skill + ".";
        } else {
            combatPhase = CombatPhase.END;
            message = combatant.getLabel() + " attacked " + target.getLabel() + " with " + weaponName + "/" +
                    weaponModeName + ", but failed to hit. Rolled a " + toHitRoll + ", but needed a " + skill + ".";
        }

        // Update combatant.
        combatant.setToHitRoll(toHitRoll);
        combatant.setToHitResultType(resultType);

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(combatPhase);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doResolveDefense(int round, int index, Combatant target, DefenseType defenseType,
            String defendingItemName) {
        String itemParenth = "";
        if (defenseType != DefenseType.DODGE && defenseType != DefenseType.NO_DEFENSE) {
            itemParenth = " (" + defendingItemName + ")";
        }
        String message = target.getLabel() + " has chosen a defense of " + defenseType + itemParenth + ".";

        // Update target.
        CombatDefense combatDefense = new CombatDefense();
        combatDefense.setDefenseType(defenseType);
        combatDefense.setDefendingItemName(defendingItemName);
        target.getCombatDefenses().add(combatDefense);

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(CombatPhase.PROMPT_FOR_TO_DEFEND);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doResolveToDefend(int round, int index, Combatant combatant, Combatant target, int toDefendRoll) {
        int size = target.getCombatDefenses().size();
        CombatDefense combatDefense = target.getCombatDefenses().get(size - 1);
        int defenseSkill = combatDefense.getToDefendEffectiveSkill();
        ResultType resultType = getResultType(defenseSkill, toDefendRoll);
        DefenseType defenseType = combatDefense.getDefenseType();
        String defendingItemName = combatDefense.getDefendingItemName();
        String itemParenth = "";
        if (defenseType != DefenseType.DODGE && defenseType != DefenseType.NO_DEFENSE) {
            itemParenth = " (" + defendingItemName + ")";
        }
        CombatPhase combatPhase;
        String message;
        if (resultType == ResultType.CRITICAL_SUCCESS || resultType == ResultType.SUCCESS) {
            combatPhase = CombatPhase.END;
            message = target.getLabel() + " successfully defended against " + combatant.getLabel() + " with " +
                    defenseType + itemParenth + ". Rolled a " + toDefendRoll + ", needed a " + defenseSkill + ".";
        } else {
            combatPhase = CombatPhase.PROMPT_FOR_DAMAGE;
            message = target.getLabel() + " failed to defend against " + combatant.getLabel() + " with " + defenseType +
                    itemParenth + ". Rolled a " + toDefendRoll + ", but needed a " + defenseSkill + ".";
        }

        // Update target.
        combatDefense.setToDefendRoll(toDefendRoll);
        combatDefense.setToDefendResult(resultType);

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(combatPhase);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    private NextStep doResolveDamage(int round, int index, Combatant combatant, Combatant target, int forDamageRoll) {
        int naturalDamageResistance = target.getGameChar().getDamageResistance();
        int armorDamageResistance = getArmorDamageResistance(HitLocation.TORSO, target.getGameChar().getArmorPieces());
        int damageResistance = naturalDamageResistance + armorDamageResistance;
        int penetratingDamage = forDamageRoll - damageResistance;
        MeleeWeapon weapon = getWeapon(combatant.getWeaponName(), combatant.getGameChar().getMeleeWeapons());
        MeleeWeaponMode weaponMode = getWeaponMode(combatant.getWeaponModeName(), weapon.getMeleeWeaponModes());
        DamageType damageType = weaponMode.getDamageType();
        double damageMultiplier = getDamageMultiplier(damageType);
        int injuryDamage = (int) (penetratingDamage * damageMultiplier);
        if (penetratingDamage > 0 && injuryDamage == 0) {
            injuryDamage = 1;
        }
        int hitPoints = target.getGameChar().getHitPoints();
        int previousDamage = target.getPreviousDamage();
        int oldCurrentDamage = target.getCurrentDamage();
        int newCurrentDamage = oldCurrentDamage + injuryDamage;
        int shockPenalty = getShockPenalty(newCurrentDamage);
        int oldRemainingHitPoints = hitPoints - oldCurrentDamage - previousDamage;
        int newRemainingHitPoints = hitPoints - newCurrentDamage - previousDamage;
        int oldHitLevel = getHitLevel(hitPoints, oldRemainingHitPoints);
        int newHitLevel = getHitLevel(hitPoints, newRemainingHitPoints);
        Boolean unconsciousnessCheckFailed = target.getUnconsciousnessCheckFailed();
        Boolean deathCheckFailed = target.getDeathCheckFailed();
        HealthStatus healthStatus = getHealthStatus(newHitLevel, unconsciousnessCheckFailed, deathCheckFailed);
        int basicMove = target.getGameChar().getBasicMove();
        int encumbranceLevel = target.getGameChar().getEncumbranceLevel();
        int newCurrentMove = getCurrentMove(healthStatus, basicMove, encumbranceLevel);

        // If the target's new status is between ALMOST and ALMOST4 and the target's old status is less and the target
        // has not yet failed a death check, then the target will need to make one or more rolls to avoid death.
        int nbrOfDeathChecksNeeded = 0;
        if (newHitLevel >= 4 && newHitLevel <= 7 && newHitLevel > oldHitLevel) {
            if (oldHitLevel <= 3) {
                oldHitLevel = 3;
            }
            nbrOfDeathChecksNeeded = newHitLevel - oldHitLevel;
        }
        CombatPhase combatPhase = CombatPhase.END;
        if (nbrOfDeathChecksNeeded > 0) {
            combatPhase = CombatPhase.PROMPT_FOR_DEATH_CHECK;
        }
        String message = target.getLabel() + " is hit for " + forDamageRoll + " " + damageType + " damage. " +
                penetratingDamage + " gets through his/her armour (DR " + damageResistance + ") doing " + injuryDamage +
                " hits of damage.";

        // Update combatant.
        combatant.setForDamageRoll(forDamageRoll);

        // Update target.
        target.setCurrentDamage(newCurrentDamage);
        target.setShockPenalty(shockPenalty);
        target.setNbrOfDeathChecksNeeded(nbrOfDeathChecksNeeded);
        target.setHealthStatus(healthStatus);
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
        int deathCheck = target.getGameChar().getDeathCheck();
        ResultType resultType = getResultType(deathCheck, rollForDeathCheck);
        int nbrOfDeathChecksNeeded = target.getNbrOfDeathChecksNeeded();
        boolean deathCheckFailed = false;
        HealthStatus healthStatus = target.getHealthStatus();
        int currentMove = target.getCurrentMove();
        CombatPhase combatPhase = CombatPhase.END;
        String message;
        if (resultType == ResultType.CRITICAL_SUCCESS || resultType == ResultType.SUCCESS) {
            nbrOfDeathChecksNeeded--;
            if (nbrOfDeathChecksNeeded > 0) {
                combatPhase = CombatPhase.PROMPT_FOR_DEATH_CHECK;
            }
            message = target.getLabel() + " successfully avoided death! Rolled a " + rollForDeathCheck + ", needed a " +
                    deathCheck + ".";
        } else {
            nbrOfDeathChecksNeeded = 0;
            deathCheckFailed = true;
            healthStatus = HealthStatus.DEAD;
            currentMove = 0;
            message = target.getLabel() + " is now dead! Rolled a " + rollForDeathCheck + ", needed a " + deathCheck +
                    ".";
        }

        // Update target.
        target.setNbrOfDeathChecksNeeded(nbrOfDeathChecksNeeded);
        target.setDeathCheckFailed(deathCheckFailed);
        target.setHealthStatus(healthStatus);
        target.setCurrentMove(currentMove);

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
        combatant.setShockPenalty(0);

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

    private int getArmorDamageResistance(HitLocation hitLocation, List<ArmorPiece> armorPieces) {
        int dr = 0;
        for (ArmorPiece armorPiece : armorPieces) {
            if (armorPiece.getHitLocations().contains(hitLocation)) {
                dr += armorPiece.getDamageResistance();
            }
        }
        return dr;
    }

    private Combatant getCombatant(String combatantLabel, List<Combatant> combatants) {
        Combatant found = null;
        for (Combatant combatant : combatants) {
            if (combatantLabel.equals(combatant.getLabel())) {
                found = combatant;
            }
        }
        return found;
    }

    private int getCurrentMove(HealthStatus healthStatus, int basicMove, int encumbranceLevel) {
        int currentMove = 0;
        if (healthStatus == HealthStatus.ALIVE) {
            currentMove = basicMove - encumbranceLevel;
        } else if (healthStatus == HealthStatus.REELING || healthStatus == HealthStatus.BARELY ||
                healthStatus == HealthStatus.ALMOST || healthStatus == HealthStatus.ALMOST2 ||
                healthStatus == HealthStatus.ALMOST3 || healthStatus == HealthStatus.ALMOST4) {
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

    private HealthStatus getHealthStatus(int hitLevel, boolean unconsciousnessCheckFailed, boolean deathCheckFailed) {
        HealthStatus healthStatus = null;
        if (hitLevel == 1) {
            healthStatus = HealthStatus.ALIVE;
        } else if (hitLevel == 2) {
            healthStatus = HealthStatus.REELING;
        } else if (hitLevel == 3) {
            healthStatus = unconsciousnessCheckFailed ? HealthStatus.UNCONSCIOUS : HealthStatus.BARELY;
        } else if (hitLevel == 4) {
            if (deathCheckFailed) {
                healthStatus = HealthStatus.DEAD;
            } else if (unconsciousnessCheckFailed) {
                healthStatus = HealthStatus.UNCONSCIOUS;
            } else {
                healthStatus = HealthStatus.ALMOST;
            }
        } else if (hitLevel == 5) {
            if (deathCheckFailed) {
                healthStatus = HealthStatus.DEAD;
            } else if (unconsciousnessCheckFailed) {
                healthStatus = HealthStatus.UNCONSCIOUS;
            } else {
                healthStatus = HealthStatus.ALMOST2;
            }
        } else if (hitLevel == 6) {
            if (deathCheckFailed) {
                healthStatus = HealthStatus.DEAD;
            } else if (unconsciousnessCheckFailed) {
                healthStatus = HealthStatus.UNCONSCIOUS;
            } else {
                healthStatus = HealthStatus.ALMOST3;
            }
        } else if (hitLevel == 7) {
            if (deathCheckFailed) {
                healthStatus = HealthStatus.DEAD;
            } else if (unconsciousnessCheckFailed) {
                healthStatus = HealthStatus.UNCONSCIOUS;
            } else {
                healthStatus = HealthStatus.ALMOST4;
            }
        } else if (hitLevel == 8) {
            healthStatus = HealthStatus.DEAD;
        } else if (hitLevel == 9) {
            healthStatus = HealthStatus.DESTROYED;
        }
        return healthStatus;
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

    private Shield getShield(String shieldName, List<Shield> shields) {
        Shield found = null;
        for (Shield shield : shields) {
            if (shieldName.equals(shield.getName())) {
                found = shield;
            }
        }
        return found;
    }

    private int getShockPenalty(int currentDamage) {
        if (currentDamage >= 4) {
            return -4;
        } else {
            return -currentDamage;
        }
    }

    private MeleeWeapon getWeapon(String weaponName, List<MeleeWeapon> weapons) {
        MeleeWeapon found = null;
        for (MeleeWeapon weapon : weapons) {
            if (weaponName.equals(weapon.getName())) {
                found = weapon;
            }
        }
        return found;
    }

    private MeleeWeaponMode getWeaponMode(String weaponModeName, List<MeleeWeaponMode> weaponModes) {
        MeleeWeaponMode found = null;
        for (MeleeWeaponMode weaponMode : weaponModes) {
            if (weaponModeName.equals(weaponMode.getName())) {
                found = weaponMode;
            }
        }
        return found;
    }
}
