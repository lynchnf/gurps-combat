package norman.gurps.combat;

import norman.gurps.character.CharacterWeapon;

/**
 * Bean that contains character weapons plus additional properties specific to combat.
 */
public class CombatantWeapon {
    private Combatant combatant;
    private CharacterWeapon weapon;
    private boolean ready;

    public CombatantWeapon(Combatant combatant, CharacterWeapon weapon) {
        this.combatant = combatant;
        this.weapon = weapon;
        ready = true;
    }
}
