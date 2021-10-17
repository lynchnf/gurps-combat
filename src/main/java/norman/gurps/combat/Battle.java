package norman.gurps.combat;

import norman.gurps.character.CharacterWeapon;
import norman.gurps.character.GameCharacter;
import norman.gurps.equipment.DamageType;
import norman.gurps.strategy.DefaultStrategyHelper;
import norman.gurps.strategy.StrategyHelper;
import norman.gurps.util.MiscUtil;
import norman.gurps.util.RollResult;
import norman.gurps.util.RollStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Battle {
    private Defense[] defenses;

    // TODO Do I really need this?
    public List<Combatant> getCombatants() {
        return combatants;
    }

    // TODO Do I really need this?
    public int getActorIndex() {
        return actorIndex;
    }

    private List<Combatant> combatants = new ArrayList<>();
    private int roundIndex;
    private int actorIndex;
    private int targetIndex;

    public void addCombatant(GameCharacter character, String side) {
        addCombatant(character, side, new DefaultStrategyHelper(this));
    }

    public void addCombatant(GameCharacter character, String side, StrategyHelper helper) {
        Combatant combatant = new Combatant();
        combatant.setCharacter(character);
        combatant.setSide(side);
        combatant.setHelper(helper);
        combatants.add(combatant);
    }

    public void start() {
        Collections.sort(combatants);
        actorIndex = -1;
    }

    public void next() {
        actorIndex++;
        if (actorIndex >= combatants.size()) {
            roundIndex++;
            actorIndex = 0;
        }
        combatants.get(actorIndex).reset();
    }

    public int getRound() {
        return roundIndex + 1;
    }

    public Combatant getActor() {
        return combatants.get(actorIndex);
    }

    public void setTargetIndex(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    public Combatant getTarget() {
        if (targetIndex < 0) {
            return null;
        } else {
            return combatants.get(targetIndex);
        }
    }

    public RollResult[] executeAction() {
        Combatant actor = combatants.get(actorIndex);
        Action action = actor.getAction();
        Maneuver maneuver = action.getManeuver();
        int effectiveSkill = actor.getCharacter().getWeapon(action.getWeaponLabel()).getAttack(action.getSkillName()) -
                actor.getShockPenalty();

        RollResult[] results = {};
        if (maneuver == Maneuver.ATTACK || maneuver == Maneuver.ALL_OUT_ATTACK_STRONG) {
            RollResult result = new RollResult();
            result.setEffectiveSkill(effectiveSkill);
            int rollValue = MiscUtil.rollDice(3);
            result.setRollValue(rollValue);
            result.setStatus(MiscUtil.calculateStatus(effectiveSkill, rollValue));
            results = new RollResult[]{result};
        } else if (maneuver == Maneuver.ALL_OUT_ATTACK_DETERMINED) {
            RollResult result = new RollResult();
            result.setEffectiveSkill(effectiveSkill + 4);
            int rollValue = MiscUtil.rollDice(3);
            result.setRollValue(rollValue);
            result.setStatus(MiscUtil.calculateStatus(effectiveSkill, rollValue));
            results = new RollResult[]{result};
        } else if (maneuver == Maneuver.ALL_OUT_ATTACK_DOUBLE) {
            RollResult result1 = new RollResult();
            result1.setEffectiveSkill(effectiveSkill);
            int rollValue1 = MiscUtil.rollDice(3);
            result1.setRollValue(rollValue1);
            result1.setStatus(MiscUtil.calculateStatus(effectiveSkill, rollValue1));
            RollResult result2 = new RollResult();
            result2.setEffectiveSkill(effectiveSkill);
            int rollValue2 = MiscUtil.rollDice(3);
            result2.setRollValue(rollValue1);
            result2.setStatus(MiscUtil.calculateStatus(effectiveSkill, rollValue2));
            results = new RollResult[]{result1, result2};
        } else if (maneuver == Maneuver.MOVE_AND_ATTACK) {
            RollResult result = new RollResult();
            result.setEffectiveSkill(Math.min(9, effectiveSkill - 4));
            int rollValue = MiscUtil.rollDice(3);
            result.setRollValue(rollValue);
            result.setStatus(MiscUtil.calculateStatus(effectiveSkill, rollValue));
            results = new RollResult[]{result};
        }
        return results;
    }

    public RollResult[] executeDefense() {
        List<RollResult> results = new ArrayList<>();
        for (Defense defense : combatants.get(targetIndex).getDefenses()) {
            DefenseType type = defense.getType();
            GameCharacter character = combatants.get(targetIndex).getCharacter();
            int defenseSkill = 0;
            if (type == DefenseType.PARRY) {
                defenseSkill = character.getWeapon(defense.getItemLabel()).getParry(defense.getSkillName());
            } else if (type == DefenseType.BLOCK) {
                defenseSkill = character.getShield(defense.getItemLabel()).getBlock();
            } else {
                defenseSkill = character.getDodge();
            }

            RollResult result = new RollResult();
            result.setEffectiveSkill(defenseSkill);
            int rollValue = MiscUtil.rollDice(3);
            result.setRollValue(rollValue);
            RollStatus rollStatus = MiscUtil.calculateSimpleStatus(defenseSkill, rollValue);
            result.setStatus(rollStatus);
            results.add(result);
            if (rollStatus == RollStatus.SUCCESS || rollStatus == RollStatus.CRITICAL_SUCCESS) {
                break;
            }
        }
        return results.toArray(new RollResult[0]);
    }

    public DamageResult applyDamage() {
        Action action = combatants.get(actorIndex).getAction();
        CharacterWeapon weapon = combatants.get(actorIndex).getCharacter().getWeapon(action.getWeaponLabel());
        String skillName = action.getSkillName();
        String modeName = action.getModeName();
        int damageDice = weapon.getDamageDice(skillName, modeName);
        int damageAdds = weapon.getDamageAdds(skillName, modeName);
        String damage = damageDice + "d";
        if (damageAdds < 0) {
            damage += String.valueOf(damageAdds);
        } else if (damageAdds > 0) {
            damage += "+" + damageAdds;
        }
        int rollValue = MiscUtil.rollDice(damageDice, damageAdds);
        Combatant target = combatants.get(targetIndex);
        int damageResistance = target.getCharacter().getArmorDamageResistance();
        DamageType damageType = weapon.getDamageType(skillName, modeName);
        int penetratingDamage = 0;
        int finalDamage = 0;
        if (rollValue > damageResistance) {
            penetratingDamage = rollValue - damageResistance;
            finalDamage = (int) (penetratingDamage * damageType.getWoundingModifier());
        }
        target.applyDamage(finalDamage);

        DamageResult result = new DamageResult();
        result.setDamage(damage);
        result.setRollValue(rollValue);
        result.setPenetratingDamage(penetratingDamage);
        result.setFinalDamage(finalDamage);
        return result;
    }

    public int getStayConscious() {
        int health = combatants.get(actorIndex).getCharacter().getHealth();
        int penalty = -combatants.get(actorIndex).getCurrentHitPoints() / health;
        return health - penalty;
    }

    public boolean over() {
        // TODO Fix this for team instead of individuals.
        boolean over = false;
        for (Combatant combatant : combatants) {
            if (combatant.getStatus() == CombatantStatus.UNCONSCIOUS) {
                over = true;
                break;
            }
        }
        return over;
    }
}
