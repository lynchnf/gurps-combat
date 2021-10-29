package norman.gurps.character;

import norman.gurps.equipment.Shield;
import norman.gurps.skill.ControllingAttribute;
import norman.gurps.skill.DifficultyLevel;
import norman.gurps.skill.Skill;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CharacterShieldTest {
    CharacterShield characterShield;

    @BeforeEach
    void setUp() {
        Skill shieldSkill = new Skill();
        shieldSkill.setName("Shield");
        shieldSkill.setControllingAttribute(ControllingAttribute.DX);
        shieldSkill.setDifficultyLevel(DifficultyLevel.EASY);

        Shield mediumShield = new Shield();
        mediumShield.setName("Medium Shield");
        mediumShield.setCost(BigDecimal.valueOf(60, 0));
        mediumShield.setWeight(15.0);
        mediumShield.setSkillName(shieldSkill.getName());
        mediumShield.setDefenseBonus(2);

        GameCharacter character = new GameCharacter();
        character.setName("Testy McTester");
        character.setStrength(9);
        character.setDexterity(13);
        character.setIntelligence(12);
        character.setHealth(14);
        character.addSkill(shieldSkill, 13);
        character.addEquipment(mediumShield);

        characterShield = character.getShield("Medium Shield");
    }

    @AfterEach
    void tearDown() {
        characterShield = null;
    }

    @Test
    void getBlock() {
        assertEquals(11, characterShield.getBlock());
    }

    @Test
    void getLabel() {
        assertEquals("Medium Shield", characterShield.getLabel());
    }

    @Test
    void getShield() {
        assertEquals("Medium Shield", characterShield.getShield().getName());
    }

    @Test
    void isPrimary() {
        assertTrue(characterShield.isPrimary());
    }
}