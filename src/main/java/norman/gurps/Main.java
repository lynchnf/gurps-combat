package norman.gurps;

import norman.gurps.character.GameCharacter;
import norman.gurps.combat.Action;
import norman.gurps.combat.Battle;
import norman.gurps.combat.Combatant;
import norman.gurps.combat.CombatantStatus;
import norman.gurps.combat.DamageResult;
import norman.gurps.combat.Defense;
import norman.gurps.equipment.Armor;
import norman.gurps.equipment.DamageType;
import norman.gurps.equipment.Shield;
import norman.gurps.equipment.Weapon;
import norman.gurps.skill.Skill;
import norman.gurps.util.MiscUtil;
import norman.gurps.util.RollResult;
import norman.gurps.util.RollStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static norman.gurps.combat.CombatantStatus.COLLAPSING;
import static norman.gurps.combat.CombatantStatus.UNCONSCIOUS;
import static norman.gurps.combat.Maneuver.ALL_OUT_ATTACK_DETERMINED;
import static norman.gurps.combat.Maneuver.ALL_OUT_ATTACK_DOUBLE;
import static norman.gurps.combat.Maneuver.ALL_OUT_ATTACK_STRONG;
import static norman.gurps.combat.Maneuver.DO_NOTHING;
import static norman.gurps.util.RollStatus.CRITICAL_SUCCESS;
import static norman.gurps.util.RollStatus.SUCCESS;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.debug("Starting Application");
        Main me = new Main();
        me.doIt();
        LOGGER.debug("Finished Application");
    }

    private void doIt() {
        Map<String, Skill> skills = MiscUtil.getSkills();
        Skill broadswordSkill = skills.get("Broadsword");
        Skill shieldSkill = skills.get("Shield");
        Armor leatherArmor = MiscUtil.getArmors().get("Leather Armor");
        Shield smallShield = MiscUtil.getShields().get("Small Shield");
        Weapon broadsword = MiscUtil.getWeapons().get("Broadsword");

        // Create characters.
        GameCharacter able = new GameCharacter();
        able.setName("Able");
        able.setStrength(10);
        able.setDexterity(10);
        able.setIntelligence(10);
        able.setHealth(10);
        able.addSkill(broadswordSkill, 12);
        able.addSkill(shieldSkill, 10);
        able.addEquipment(leatherArmor);
        able.addEquipment(smallShield);
        able.addEquipment(broadsword);

        GameCharacter baker = new GameCharacter();
        baker.setName("Baker");
        baker.setStrength(10);
        baker.setDexterity(10);
        baker.setIntelligence(10);
        baker.setHealth(10);
        baker.addSkill(broadswordSkill, 12);
        baker.addSkill(shieldSkill, 10);
        baker.addEquipment(leatherArmor);
        baker.addEquipment(smallShield);
        baker.addEquipment(broadsword);

        // Create combat.
        Battle battle = new Battle();

        // Add characters to combat.
        battle.addCombatant(able, "blue");
        battle.addCombatant(baker, "red");

        // Start combat;
        battle.start();

        // Continue until the battle is over.
        do {
            // Reset combatant.
            CombatantStatus actorStatus = doReset(battle);

            // If our guy is not unconscious, ...
            if (actorStatus != UNCONSCIOUS) {

                // Decide action.
                Action decideAction = battle.getActor().getHelper().decideAction();
                battle.getActor().setAction(decideAction);

                // Can our guy stay conscious?
                actorStatus = doStayConscious(battle);
            }

            // If our guy is unconscious, skip the rest of this loop.
            if (actorStatus == UNCONSCIOUS) {
                continue;
            }

            // If this action requires a target, decide on one.
            battle.setTargetIndex(-1);
            if (battle.getActor().getAction().getManeuver().isTargetRequired()) {
                int decideTarget = battle.getActor().getHelper().decideTarget();
                battle.setTargetIndex(decideTarget);
            }

            // Execute action.
            RollResult[] actionResults = doAction(battle);

            for (RollResult actionResult : actionResults) {
                boolean defenseSuccess = false;
                boolean actionSuccess = doActionResult(actionResult);

                // If defense needed ...
                if (isDefenseNeeded(battle, actionResult)) {

                    // Decide defense(s).
                    Defense[] decideDefense = battle.getTarget().getHelper().decideDefense();
                    battle.getTarget().setDefenses(decideDefense);

                    // Execute defense.
                    defenseSuccess = doDefense(battle);
                }

                // If the action was successful and the defense was not, calculate and apply damage.
                if (actionSuccess && !defenseSuccess) {
                    doDamage(battle);
                }
            }
        } while (!battle.over());
    }

    private CombatantStatus doStayConscious(Battle battle) {
        // If our guy is collapsing and he is not doing nothing, he need to make a roll to avoid unconsciousness.
        Combatant actor = battle.getActor();
        CombatantStatus actorStatus = actor.getStatus();
        if (actorStatus == COLLAPSING) {
            String actorName = actor.getCharacter().getName();
            if (actor.getAction().getManeuver() == DO_NOTHING) {
                System.out.printf("%s is able to stay conscious by doing nothing.%n", actorName);
            } else {
                System.out.printf("%s attempts to stay conscious.%n", actorName);
                RollResult result = battle.getStayConscious();
                if (result.getStatus() == SUCCESS || result.getStatus() == CRITICAL_SUCCESS) {
                    System.out.printf("%s! Made it by %d. Needed a %d and rolled a %d.%n", result.getStatus().name(),
                            result.getEffectiveSkill() - result.getRollValue(), result.getEffectiveSkill(),
                            result.getRollValue());
                } else {
                    System.out.printf("%s! Failed by %d. Needed a %d, but rolled a %d.%n", result.getStatus().name(),
                            result.getRollValue() - result.getEffectiveSkill(), result.getEffectiveSkill(),
                            result.getRollValue());
                    actor.setUnconscious();
                    actorStatus = UNCONSCIOUS;
                }
            }
        }
        return actorStatus;
    }

    private CombatantStatus doReset(Battle battle) {
        battle.next();
        Combatant actor = battle.getActor();
        String actorName = actor.getCharacter().getName();
        CombatantStatus actorStatus = actor.getStatus();
        int shockPenalty = actor.getShockPenalty();
        int currentHitPoints = actor.getCurrentHitPoints();
        if (actorStatus == UNCONSCIOUS) {
            System.out.printf("Round %d, %s's turn, but he is currently %s.%n", battle.getRound(), actorName,
                    actorStatus);
        } else {
            if (shockPenalty > 0) {
                System.out
                        .printf("Round %d, %s's turn. He is currently %s with %d hit points and is at -%d to all DX and IQ based skills.%n",
                                battle.getRound(), actorName, actorStatus, currentHitPoints, shockPenalty);
            } else {
                System.out.printf("Round %d, %s's turn. He is currently %s with %d hit points.%n", battle.getRound(),
                        actorName, actorStatus, currentHitPoints);
            }
        }
        return actorStatus;
    }

    private RollResult[] doAction(Battle battle) {
        Combatant actor = battle.getActor();
        String actorName = actor.getCharacter().getName();
        Action action = actor.getAction();
        if (battle.getTarget() != null) {
            String targetName = battle.getTarget().getCharacter().getName();
            System.out.printf("%s %s %s with %s (%s/%s).%n", actorName, action.getManeuver().name(), targetName,
                    action.getWeaponLabel(), action.getSkillName(), action.getModeName());
        } else {
            System.out.printf("%s %s.%n", actorName, action.getManeuver().name());
        }

        return battle.executeAction();
    }

    private boolean doActionResult(RollResult actionResult) {
        boolean actionSuccess = true;
        int actionEffectiveSkill = actionResult.getEffectiveSkill();
        int actionRollValue = actionResult.getRollValue();
        RollStatus actionStatus = actionResult.getStatus();
        if (actionStatus == SUCCESS || actionStatus == CRITICAL_SUCCESS) {
            System.out.printf("%s! Made it by %d. Needed a %d and rolled a %d.%n", actionStatus.name(),
                    actionEffectiveSkill - actionRollValue, actionEffectiveSkill, actionRollValue);
        } else {
            actionSuccess = false;
            System.out.printf("%s! Failed by %d. Needed a %d, but rolled a %d.%n", actionStatus.name(),
                    actionRollValue - actionEffectiveSkill, actionEffectiveSkill, actionRollValue);
        }
        return actionSuccess;
    }

    private boolean isDefenseNeeded(Battle battle, RollResult actionResult) {
        // If action was critically successful, defense automatically fails.

        // For each action result, the target gets a chance to defend if:
        // 1) action was successful, but not critically successful
        // 2) there is a target
        // 3) the target's last action was not some kind of all out attack.
        Action targetLastAction = battle.getTarget().getAction();
        RollStatus actionStatus = actionResult.getStatus();

        // @formatter:off
        return actionStatus == SUCCESS &&
                        battle.getTarget() != null &&
                        targetLastAction == null ||
                actionStatus == SUCCESS &&
                        battle.getTarget() != null &&
                        targetLastAction != null &&
                        targetLastAction.getManeuver() != ALL_OUT_ATTACK_DETERMINED &&
                        targetLastAction.getManeuver() != ALL_OUT_ATTACK_DOUBLE &&
                        targetLastAction.getManeuver() != ALL_OUT_ATTACK_STRONG;
        // @formatter:on
    }

    private boolean doDefense(Battle battle) {
        RollResult[] results = battle.executeDefense();

        Combatant target = battle.getTarget();
        String targetName = target.getCharacter().getName();
        Defense[] defenses = target.getDefenses();

        boolean defenseSuccess = false;
        for (int i = 0; i < results.length; i++) {
            Defense defense = defenses[i];
            RollResult result = results[i];
            if (defense.getSkillName() != null) {
                System.out.printf("%s attempted to %s with %s using skill %s.%n", targetName, defense.getType(),
                        defense.getItemLabel(), defense.getSkillName());
            } else if (defense.getItemLabel() != null) {
                System.out
                        .printf("%s attempted to %s with %s.%n", targetName, defense.getType(), defense.getItemLabel());
            } else {
                System.out.printf("%s attempted to %s.%n", targetName, defense.getType());
            }
            int defenseEffectiveSkill = result.getEffectiveSkill();
            int defenseRollValue = result.getRollValue();
            if (result.getStatus() == SUCCESS || result.getStatus() == CRITICAL_SUCCESS) {
                defenseSuccess = true;
                System.out.printf("%s! Made it by %d. Needed a %d and rolled a %d.%n", result.getStatus().name(),
                        defenseEffectiveSkill - defenseRollValue, defenseEffectiveSkill, defenseRollValue);
            } else {
                System.out.printf("%s! Failed by %d. Needed a %d, but rolled a %d.%n", result.getStatus().name(),
                        defenseRollValue - defenseEffectiveSkill, defenseEffectiveSkill, defenseRollValue);
            }
        }
        return defenseSuccess;
    }

    private void doDamage(Battle battle) {
        DamageResult damageResult = battle.applyDamage();

        Combatant actor = battle.getActor();
        String actorName = actor.getCharacter().getName();
        Action action = actor.getAction();
        DamageType damageType = actor.getCharacter().getWeapon(action.getWeaponLabel())
                .getDamageType(action.getSkillName(), action.getModeName());

        Combatant target = battle.getTarget();
        String targetName = target.getCharacter().getName();

        if (damageResult.getFinalDamage() > 0) {
            System.out
                    .printf("%s rolls %s doing %d basic damage (%s). %s penetrates doing %d total damage.%n", actorName,
                            damageResult.getDamage(), damageResult.getRollValue(), damageType.name(),
                            damageResult.getPenetratingDamage(), damageResult.getFinalDamage());
            System.out.printf("%s now has %d hit points.%n", targetName, target.getCurrentHitPoints());
        } else {
            System.out.printf("%s rolls %s doing %d basic damage (%s). %s takes no damage!%n", actorName,
                    damageResult.getDamage(), damageResult.getRollValue(), damageType.name(), targetName);
        }
    }
}
