package norman.gurps.combat.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CombatantTest {
    GameChar testGameChar;

    @BeforeEach
    void setUp() {
        testGameChar = new GameChar();
        testGameChar.setName("Bob the Example");
        testGameChar.setStrength(14);
        testGameChar.setDexterity(13);
        testGameChar.setIntelligence(12);
        testGameChar.setHealth(11);
        testGameChar.setHitPoints(15);
        testGameChar.setBasicSpeed(6.25);
        testGameChar.setBasicMove(7);
        testGameChar.setEncumbranceLevel(1);
        MeleeWeapon weapon = new MeleeWeapon();
        weapon.setName("Broadsword");
        weapon.setSkill(14);
        MeleeWeaponMode swing = new MeleeWeaponMode();
        swing.setName("swing");
        swing.setDamageDice(2);
        swing.setDamageAdds(1);
        swing.setDamageType(DamageType.CUTTING);
        swing.getReaches().add(1);
        weapon.getModes().add(swing);
        MeleeWeaponMode thrust = new MeleeWeaponMode();
        thrust.setName("thrust");
        thrust.setDamageDice(1);
        thrust.setDamageAdds(1);
        thrust.setDamageType(DamageType.CRUSHING);
        thrust.getReaches().add(1);
        weapon.getModes().add(thrust);
        weapon.setParryType(ParryType.YES);
        weapon.setParryModifier(0);
        weapon.setMinStrength(10);
        weapon.setTwoHanded(false);
        testGameChar.getMeleeWeapons().add(weapon);
        Shield shield = new Shield();
        shield.setName("Medium Shield");
        shield.setSkill(13);
        shield.setDefenseBonus(2);
        testGameChar.getShields().add(shield);
        Armor torso = new Armor();
        torso.setLocation(Location.TORSO);
        torso.setDamageResistance(1);
        testGameChar.getArmorList().add(torso);
        Armor groin = new Armor();
        groin.setLocation(Location.GROIN);
        groin.setDamageResistance(1);
        testGameChar.getArmorList().add(groin);
        Armor legs = new Armor();
        legs.setLocation(Location.LEGS);
        legs.setDamageResistance(1);
        testGameChar.getArmorList().add(legs);
        Armor arms = new Armor();
        arms.setLocation(Location.ARMS);
        arms.setDamageResistance(1);
        testGameChar.getArmorList().add(arms);
        Armor skull = new Armor();
        skull.setLocation(Location.SKULL);
        skull.setDamageResistance(1);
        testGameChar.getArmorList().add(skull);
        Armor face = new Armor();
        face.setLocation(Location.FACE);
        face.setDamageResistance(1);
        testGameChar.getArmorList().add(face);
        Armor hands = new Armor();
        hands.setLocation(Location.HANDS);
        hands.setDamageResistance(1);
        testGameChar.getArmorList().add(hands);
        Armor feet = new Armor();
        feet.setLocation(Location.FEET);
        feet.setDamageResistance(1);
        testGameChar.getArmorList().add(feet);
    }

    @Test
    void constructor1() {
        Combatant combatant = new Combatant(testGameChar, new HashSet<>());

        assertEquals("Bob the Example", combatant.getLabel());
    }

    @Test
    void constructor2() {
        Set<String> existingLabels = new HashSet<>();
        existingLabels.add("Bob the Example");

        Combatant combatant = new Combatant(testGameChar, existingLabels);

        assertEquals("Bob the Example 2", combatant.getLabel());
    }

    @Test
    void constructor3() {
        Set<String> existingLabels = new HashSet<>();
        existingLabels.add("Bob the Example");
        existingLabels.add("Bob the Example 2");

        Combatant combatant = new Combatant(testGameChar, existingLabels);

        assertEquals("Bob the Example 3", combatant.getLabel());
    }
}