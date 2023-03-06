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
        testGameChar.setName("Test Character");
        testGameChar.setStrength(14);
        testGameChar.setDexterity(13);
        testGameChar.setIntelligence(12);
        testGameChar.setHealth(11);
        testGameChar.setHitPoints(15);
        testGameChar.setBasicSpeed(6.25);
        testGameChar.setBasicMove(7);
        testGameChar.setEncumbranceLevel(2);
        MeleeWeapon weapon = new MeleeWeapon();
        weapon.setName("Broadsword");
        weapon.setSkill(13);
        MeleeWeaponMode swing = new MeleeWeaponMode();
        swing.setName("swing");
        swing.setDamageDice(2);
        swing.setDamageAdds(1);
        swing.setDamageType(DamageType.CUTTING);
        swing.getReaches().add(1);
        swing.setParryType(ParryType.YES);
        swing.setParryModifier(0);
        weapon.getModes().add(swing);
        MeleeWeaponMode thrust = new MeleeWeaponMode();
        thrust.setName("thrust");
        thrust.setDamageDice(1);
        thrust.setDamageAdds(1);
        thrust.setDamageType(DamageType.CRUSHING);
        thrust.getReaches().add(1);
        thrust.setParryType(ParryType.YES);
        thrust.setParryModifier(0);
        weapon.getModes().add(thrust);
        weapon.setMinStrength(10);
        testGameChar.getMeleeWeapons().add(weapon);
        Shield shield = new Shield();
        shield.setName("Medium Shield");
        shield.setSkill(13);
        shield.setDefenseBonus(2);
        testGameChar.setShield(shield);
        Armor torso = new Armor();
        torso.setLocation(Location.TORSO);
        torso.setDamageResistance(2);
        testGameChar.getArmorList().add(torso);
        Armor groin = new Armor();
        groin.setLocation(Location.GROIN);
        groin.setDamageResistance(2);
        testGameChar.getArmorList().add(groin);
        Armor legs = new Armor();
        legs.setLocation(Location.LEGS);
        legs.setDamageResistance(2);
        testGameChar.getArmorList().add(legs);
        Armor arms = new Armor();
        arms.setLocation(Location.ARMS);
        arms.setDamageResistance(2);
        testGameChar.getArmorList().add(arms);
        Armor skull = new Armor();
        skull.setLocation(Location.SKULL);
        skull.setDamageResistance(2);
        testGameChar.getArmorList().add(skull);
        Armor face = new Armor();
        face.setLocation(Location.FACE);
        face.setDamageResistance(2);
        testGameChar.getArmorList().add(face);
        Armor hands = new Armor();
        hands.setLocation(Location.HANDS);
        hands.setDamageResistance(2);
        testGameChar.getArmorList().add(hands);
        Armor feet = new Armor();
        feet.setLocation(Location.FEET);
        feet.setDamageResistance(2);
        testGameChar.getArmorList().add(feet);
    }

    @Test
    void constructor1() {
        Combatant combatant = new Combatant(testGameChar, new HashSet<>());

        assertEquals("Test Character", combatant.getLabel());
    }

    @Test
    void constructor2() {
        Set<String> existingLabels = new HashSet<>();
        existingLabels.add("Test Character");

        Combatant combatant = new Combatant(testGameChar, existingLabels);

        assertEquals("Test Character 2", combatant.getLabel());
    }

    @Test
    void constructor3() {
        Set<String> existingLabels = new HashSet<>();
        existingLabels.add("Test Character");
        existingLabels.add("Test Character 2");

        Combatant combatant = new Combatant(testGameChar, existingLabels);

        assertEquals("Test Character 3", combatant.getLabel());
    }
}