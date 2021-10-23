package norman.gurps.character;

import norman.gurps.equipment.Armor;
import norman.gurps.equipment.DamageBase;
import norman.gurps.equipment.DamageType;
import norman.gurps.equipment.Item;
import norman.gurps.equipment.Shield;
import norman.gurps.equipment.Weapon;
import norman.gurps.equipment.WeaponMode;
import norman.gurps.equipment.WeaponSkill;
import norman.gurps.skill.ControllingAttribute;
import norman.gurps.skill.DifficultyLevel;
import norman.gurps.skill.Skill;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class GameCharacterTest {
    private GameCharacter character;

    @BeforeEach
    void setUp() {
        Skill broadswordSkill = new Skill();
        broadswordSkill.setName("Broadsword");
        broadswordSkill.setControllingAttribute(ControllingAttribute.DX);
        broadswordSkill.setDifficultyLevel(DifficultyLevel.AVERAGE);

        Skill knifeSkill = new Skill();
        knifeSkill.setName("Knife");
        knifeSkill.setControllingAttribute(ControllingAttribute.DX);
        knifeSkill.setDifficultyLevel(DifficultyLevel.EASY);
        knifeSkill.setParryWithAdjustment(-1);

        Skill shieldSkill = new Skill();
        shieldSkill.setName("Shield");
        shieldSkill.setControllingAttribute(ControllingAttribute.DX);
        shieldSkill.setDifficultyLevel(DifficultyLevel.EASY);

        Armor leatherArmor = new Armor();
        leatherArmor.setName("Leather Armor");
        leatherArmor.setCost(BigDecimal.valueOf(340, 0));
        leatherArmor.setWeight(19.5);
        leatherArmor.setDamageResistance(2);

        Shield smallShield = new Shield();
        smallShield.setName("Small Shield");
        smallShield.setCost(BigDecimal.valueOf(40, 0));
        smallShield.setWeight(8.0);
        smallShield.setSkillName(shieldSkill.getName());
        smallShield.setDefenseBonus(1);

        Weapon broadsword = new Weapon();
        broadsword.setName("Broadsword");
        broadsword.setCost(BigDecimal.valueOf(500, 0));
        broadsword.setWeight(3.0);

        WeaponSkill broadswordWeaponSkill = new WeaponSkill();
        broadswordWeaponSkill.setSkillName(broadswordSkill.getName());
        broadswordWeaponSkill.setMinimumStrength(10);
        broadsword.getSkills().put(broadswordSkill.getName(), broadswordWeaponSkill);

        WeaponMode swing = new WeaponMode();
        swing.setModeName("Swing");
        swing.setDamageBase(DamageBase.SWING);
        swing.setDamageAdds(1);
        swing.setDamageType(DamageType.CUTTING);
        broadswordWeaponSkill.getModes().put(swing.getModeName(), swing);

        WeaponMode thrust = new WeaponMode();
        thrust.setModeName("Thrust");
        thrust.setDamageBase(DamageBase.THRUST);
        thrust.setDamageAdds(1);
        thrust.setDamageType(DamageType.CRUSHING);
        broadswordWeaponSkill.getModes().put(thrust.getModeName(), thrust);

        Weapon knife = new Weapon();
        knife.setName("Large Knife");
        knife.setCost(BigDecimal.valueOf(40, 0));
        knife.setWeight(1.0);

        WeaponSkill knifeWeaponSkill = new WeaponSkill();
        knifeWeaponSkill.setSkillName(knifeSkill.getName());
        knifeWeaponSkill.setMinimumStrength(6);
        knife.getSkills().put(knifeSkill.getName(), knifeWeaponSkill);

        WeaponMode cut = new WeaponMode();
        cut.setModeName("Swing");
        cut.setDamageBase(DamageBase.SWING);
        cut.setDamageDice(0);
        cut.setDamageAdds(-2);
        cut.setDamageType(DamageType.CUTTING);
        knifeWeaponSkill.getModes().put(cut.getModeName(), cut);

        WeaponMode stab = new WeaponMode();
        stab.setModeName("Thrust");
        stab.setDamageBase(DamageBase.THRUST);
        stab.setDamageDice(0);
        stab.setDamageAdds(0);
        stab.setDamageType(DamageType.IMPALING);
        knifeWeaponSkill.getModes().put(stab.getModeName(), stab);

        // Create characters.
        character = new GameCharacter();
        character.setName("Testy McTester");
        character.setStrength(9);
        character.setDexterity(13);
        character.setIntelligence(12);
        character.setHealth(14);
        character.addSkill(broadswordSkill, 1);
        character.addSkill(knifeSkill, 2);
        character.addSkill(shieldSkill, 4);
        character.addEquipment(leatherArmor);
        character.addEquipment(smallShield);
        character.addEquipment(broadsword);
        character.addEquipment(knife);
    }

    @AfterEach
    void tearDown() {
        character = null;
    }

    @Test
    void getName() {
        assertEquals("Testy McTester", character.getName());
    }

    @Test
    void getStrength() {
        assertEquals(9, character.getStrength());
    }

    @Test
    void getDexterity() {
        assertEquals(13, character.getDexterity());
    }

    @Test
    void getIntelligence() {
        assertEquals(12, character.getIntelligence());
    }

    @Test
    void getHealth() {
        assertEquals(14, character.getHealth());
    }

    @Test
    void getBasicLift() {
        assertEquals(16.0, character.getBasicLift(), 0.001);
    }

    @Test
    void getBasicLiftLowStrength() {
        character.setStrength(7);
        assertEquals(9.8, character.getBasicLift(), 0.001);
    }

    @Test
    void getHitPoints() {
        assertEquals(9, character.getHitPoints());
    }

    @Test
    void getWill() {
        assertEquals(12, character.getWill());
    }

    @Test
    void getPerception() {
        assertEquals(12, character.getPerception());
    }

    @Test
    void getFatiguePoints() {
        assertEquals(14, character.getFatiguePoints());
    }

    @Test
    void getBasicSpeed() {
        assertEquals(6.75, character.getBasicSpeed(), 0.001);
    }

    @Test
    void getBasicMove() {
        assertEquals(6, character.getBasicMove());
    }

    @Test
    void getEncumbranceLevel() {
        assertEquals(1, character.getEncumbranceLevel());
    }

    @Test
    void getMove() {
        assertEquals(4, character.getMove());
    }

    @Test
    void getThrustDamageDice() {
        assertEquals(1, character.getThrustDamageDice());
    }

    @Test
    void getThrustDamageAdds() {
        assertEquals(-2, character.getThrustDamageAdds());
    }

    @Test
    void getSwingDamageDice() {
        assertEquals(1, character.getSwingDamageDice());
    }

    @Test
    void getSwingDamageAdds() {
        assertEquals(-1, character.getSwingDamageAdds());
    }

    @Test
    void getSkill() {
        CharacterSkill broadsword = character.getSkill("Broadsword");
        assertEquals(1, broadsword.getPoints());
        assertEquals(12, broadsword.getLevel());
        CharacterSkill knife = character.getSkill("Knife");
        assertEquals(2, knife.getPoints());
        assertEquals(14, knife.getLevel());
        CharacterSkill shield = character.getSkill("Shield");
        assertEquals(4, shield.getPoints());
        assertEquals(15, shield.getLevel());
    }

    @Test
    void getItem() {
        Item armor = character.getItem("Leather Armor");
        assertTrue(BigDecimal.valueOf(34000, 2).compareTo(armor.getCost()) == 0);
        assertEquals(19.5, armor.getWeight(), 0.001);
        Item shield = character.getItem("Small Shield");
        assertTrue(BigDecimal.valueOf(4000, 2).compareTo(shield.getCost()) == 0);
        assertEquals(8.0, shield.getWeight(), 0.001);
        Item sword = character.getItem("Broadsword");
        assertTrue(BigDecimal.valueOf(50000, 2).compareTo(sword.getCost()) == 0);
        assertEquals(3.0, sword.getWeight(), 0.001);
        Item knife = character.getItem("Large Knife");
        assertTrue(BigDecimal.valueOf(4000, 2).compareTo(knife.getCost()) == 0);
        assertEquals(1.0, knife.getWeight(), 0.001);
    }

    @Test
    void getArmor() {
        Armor armor = character.getArmor("Leather Armor");
        assertEquals("Leather Armor", armor.getName());
    }

    @Test
    void getShield() {
        CharacterShield shield = character.getShield("Small Shield");
        assertEquals("Small Shield", shield.getShield().getName());
        assertTrue(shield.isPrimary());
    }

    @Test
    void getPrimaryShield() {
        CharacterShield shield = character.getPrimaryShield();
        assertEquals("Small Shield", shield.getShield().getName());
    }

    @Test
    void getWeapon() {
        CharacterWeapon sword = character.getWeapon("Broadsword");
        assertEquals("Broadsword", sword.getWeapon().getName());
        assertTrue(sword.isPrimary());

        CharacterWeapon knife = character.getWeapon("Large Knife");
        assertEquals("Large Knife", knife.getWeapon().getName());
        assertFalse(knife.isPrimary());
    }

    @Test
    void getPrimaryWeapon() {
        CharacterWeapon sword = character.getPrimaryWeapon();
        assertEquals("Broadsword", sword.getWeapon().getName());
    }

    @Test
    void getShieldDefenseBonus() {
        assertEquals(1, character.getShieldDefenseBonus());
    }

    @Test
    void getShieldDefenseBonusTwoShields() {
        Shield mediumShield = new Shield();
        mediumShield.setName("Medium Shield");
        mediumShield.setCost(BigDecimal.valueOf(60, 0));
        mediumShield.setWeight(15.0);
        mediumShield.setSkillName("Shield");
        mediumShield.setDefenseBonus(2);
        character.addEquipment(mediumShield);

        assertEquals(3, character.getShieldDefenseBonus());
    }

    @Test
    void getDodge() {
        assertEquals(9, character.getDodge());
    }

    @Test
    void getArmorDamageResistance() {
        assertEquals(2, character.getArmorDamageResistance());
    }
}