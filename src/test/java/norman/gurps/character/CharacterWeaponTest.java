package norman.gurps.character;

import norman.gurps.equipment.DamageBase;
import norman.gurps.equipment.DamageType;
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

class CharacterWeaponTest {
    CharacterWeapon quarterstaff;
    CharacterWeapon axe;

    @BeforeEach
    void setUp() {
        Skill twoHandedSwordSkill = new Skill();
        twoHandedSwordSkill.setName("Two-Handed Sword");
        twoHandedSwordSkill.setControllingAttribute(ControllingAttribute.DX);
        twoHandedSwordSkill.setDifficultyLevel(DifficultyLevel.AVERAGE);

        Skill staffSkill = new Skill();
        staffSkill.setName("Staff");
        staffSkill.setControllingAttribute(ControllingAttribute.DX);
        staffSkill.setDifficultyLevel(DifficultyLevel.AVERAGE);

        Skill axeMaceSkill = new Skill();
        axeMaceSkill.setName("Axe/Mace");
        axeMaceSkill.setControllingAttribute(ControllingAttribute.DX);
        axeMaceSkill.setDifficultyLevel(DifficultyLevel.AVERAGE);

        Weapon quarterstaff = new Weapon();
        quarterstaff.setName("Quarterstaff");
        quarterstaff.setCost(BigDecimal.valueOf(10, 0));
        quarterstaff.setWeight(4.0);

        WeaponSkill staffWeaponSkill = new WeaponSkill();
        staffWeaponSkill.setSkillName(staffSkill.getName());
        staffWeaponSkill.setMinimumStrength(7);
        quarterstaff.getSkills().put(staffSkill.getName(), staffWeaponSkill);

        WeaponMode swing = new WeaponMode();
        swing.setModeName("Swing");
        swing.setDamageBase(DamageBase.SWING);
        swing.setDamageAdds(2);
        swing.setDamageType(DamageType.CRUSHING);
        staffWeaponSkill.getModes().put(swing.getModeName(), swing);

        WeaponMode thrust = new WeaponMode();
        thrust.setModeName("Thrust");
        thrust.setDamageBase(DamageBase.THRUST);
        thrust.setDamageAdds(2);
        thrust.setDamageType(DamageType.CRUSHING);
        staffWeaponSkill.getModes().put(thrust.getModeName(), thrust);

        WeaponSkill twoHandedSwordWeaponSkill = new WeaponSkill();
        twoHandedSwordWeaponSkill.setSkillName(twoHandedSwordSkill.getName());
        twoHandedSwordWeaponSkill.setMinimumStrength(9);
        quarterstaff.getSkills().put(twoHandedSwordSkill.getName(), twoHandedSwordWeaponSkill);

        WeaponMode cut = new WeaponMode();
        cut.setModeName("Swing");
        cut.setDamageBase(DamageBase.SWING);
        cut.setDamageAdds(2);
        cut.setDamageType(DamageType.CRUSHING);
        twoHandedSwordWeaponSkill.getModes().put(cut.getModeName(), cut);

        WeaponMode stab = new WeaponMode();
        stab.setModeName("Thrust");
        stab.setDamageBase(DamageBase.THRUST);
        stab.setDamageAdds(1);
        stab.setDamageType(DamageType.CRUSHING);
        twoHandedSwordWeaponSkill.getModes().put(stab.getModeName(), stab);

        Weapon axe = new Weapon();
        axe.setName("Axe");
        axe.setCost(BigDecimal.valueOf(50, 0));
        axe.setWeight(4.0);

        WeaponSkill axeMaceWeaponSkill = new WeaponSkill();
        axeMaceWeaponSkill.setSkillName(axeMaceSkill.getName());
        axeMaceWeaponSkill.setMinimumStrength(11);
        axe.getSkills().put(axeMaceSkill.getName(), axeMaceWeaponSkill);

        WeaponMode chop = new WeaponMode();
        chop.setModeName("Swing");
        chop.setDamageBase(DamageBase.SWING);
        chop.setDamageAdds(2);
        chop.setDamageType(DamageType.CUTTING);
        axeMaceWeaponSkill.getModes().put(chop.getModeName(), chop);

        GameCharacter character = new GameCharacter();
        character.setName("Testy McTester");
        character.setStrength(9);
        character.setDexterity(13);
        character.setIntelligence(12);
        character.setHealth(14);
        character.addSkill(twoHandedSwordSkill, 11);
        character.addSkill(staffSkill, 13);
        character.addSkill(axeMaceSkill, 15);
        character.addEquipment(quarterstaff);
        character.addEquipment(axe);

        this.quarterstaff = character.getWeapon(quarterstaff.getName());
        this.axe = character.getWeapon(axe.getName());
    }

    @AfterEach
    void tearDown() {
        quarterstaff = null;
        axe = null;
    }

    @Test
    void getAttack() {
        assertEquals(11, quarterstaff.getAttack("Two-Handed Sword"));
        assertEquals(13, quarterstaff.getAttack("Staff"));
        assertEquals(13, axe.getAttack("Axe/Mace"));
    }

    @Test
    void getParry() {
        assertEquals(8, quarterstaff.getParry("Two-Handed Sword"));
        assertEquals(9, quarterstaff.getParry("Staff"));
        assertEquals(9, axe.getParry("Axe/Mace"));
    }

    @Test
    void getDamageDice() {
        assertEquals(1, quarterstaff.getDamageDice("Two-Handed Sword", "Swing"));
        assertEquals(1, quarterstaff.getDamageDice("Two-Handed Sword", "Thrust"));
        assertEquals(1, quarterstaff.getDamageDice("Staff", "Swing"));
        assertEquals(1, quarterstaff.getDamageDice("Staff", "Thrust"));
        assertEquals(1, axe.getDamageDice("Axe/Mace", "Swing"));
    }

    @Test
    void getDamageAdds() {
        assertEquals(1, quarterstaff.getDamageAdds("Two-Handed Sword", "Swing"));
        assertEquals(-1, quarterstaff.getDamageAdds("Two-Handed Sword", "Thrust"));
        assertEquals(1, quarterstaff.getDamageAdds("Staff", "Swing"));
        assertEquals(0, quarterstaff.getDamageAdds("Staff", "Thrust"));
        assertEquals(1, axe.getDamageAdds("Axe/Mace", "Swing"));
    }

    @Test
    void getDamageType() {
        assertEquals(DamageType.CRUSHING, quarterstaff.getDamageType("Two-Handed Sword", "Swing"));
        assertEquals(DamageType.CRUSHING, quarterstaff.getDamageType("Two-Handed Sword", "Thrust"));
        assertEquals(DamageType.CRUSHING, quarterstaff.getDamageType("Staff", "Swing"));
        assertEquals(DamageType.CRUSHING, quarterstaff.getDamageType("Staff", "Thrust"));
        assertEquals(DamageType.CUTTING, axe.getDamageType("Axe/Mace", "Swing"));
    }

    @Test
    void getLabel() {
        assertEquals("Quarterstaff", quarterstaff.getLabel());
        assertEquals("Axe", axe.getLabel());
    }

    @Test
    void getWeapon() {
        assertEquals("Quarterstaff", quarterstaff.getWeapon().getName());
        assertEquals("Axe", axe.getWeapon().getName());
    }

    @Test
    void isPrimary() {
        assertTrue(quarterstaff.isPrimary());
        assertFalse(axe.isPrimary());
    }

    @Test
    void getPrimarySkillName() {
        assertEquals("Staff", quarterstaff.getPrimarySkillName());
        assertEquals("Axe/Mace", axe.getPrimarySkillName());
    }

    @Test
    void getPrimaryModeName() {
        assertEquals("Swing", quarterstaff.getPrimaryModeName());
        assertEquals("Swing", axe.getPrimaryModeName());
    }
}