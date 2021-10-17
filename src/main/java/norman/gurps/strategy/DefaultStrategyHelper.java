package norman.gurps.strategy;

import norman.gurps.character.CharacterShield;
import norman.gurps.character.CharacterWeapon;
import norman.gurps.combat.Action;
import norman.gurps.combat.Battle;
import norman.gurps.combat.Combatant;
import norman.gurps.combat.Defense;
import norman.gurps.combat.DefenseType;
import norman.gurps.combat.Maneuver;

import java.util.List;

public class DefaultStrategyHelper implements StrategyHelper {
    private Battle battle;

    public DefaultStrategyHelper(Battle battle) {
        this.battle = battle;
    }

    @Override
    public Action decideAction() {
        CharacterWeapon weapon = battle.getActor().getCharacter().getPrimaryWeapon();
        Action action = new Action();
        action.setManeuver(Maneuver.ATTACK);
        action.setWeaponLabel(weapon.getLabel());
        action.setSkillName(weapon.getPrimarySkillName());
        action.setModeName(weapon.getPrimaryModeName());
        return action;
    }

    @Override
    public int decideTarget() {
        List<Combatant> combatants = battle.getCombatants();
        int actorIndex = battle.getActorIndex();
        String actorSide = combatants.get(actorIndex).getSide();
        int targetIndex = -1;
        for (int i = 0; i < combatants.size(); i++) {
            Combatant combatant = combatants.get(i);
            if (!combatant.getSide().equals(actorSide)) {
                targetIndex = i;
                break;
            }
        }
        return targetIndex;
    }

    @Override
    public Defense[] decideDefense() {
        Defense defense = new Defense();
        CharacterShield shield = battle.getTarget().getCharacter().getPrimaryShield();
        if (shield != null) {
            defense.setType(DefenseType.BLOCK);
            defense.setItemLabel(shield.getLabel());
        } else {
            CharacterWeapon weapon = battle.getTarget().getCharacter().getPrimaryWeapon();
            if (weapon != null) {
                defense.setType(DefenseType.PARRY);
                defense.setItemLabel(weapon.getLabel());
                defense.setSkillName(weapon.getPrimarySkillName());
            } else {
                defense.setType(DefenseType.DODGE);
            }
        }
        return new Defense[]{defense};
    }
}
