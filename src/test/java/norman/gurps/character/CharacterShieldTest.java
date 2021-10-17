package norman.gurps.character;

import norman.gurps.equipment.Shield;
import norman.gurps.skill.ControllingAttribute;
import norman.gurps.skill.DifficultyLevel;
import norman.gurps.skill.Skill;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class CharacterShieldTest {
    private CharacterShield characterShield;

    @Before
    public void setUp() throws Exception {
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
        character.addSkill(shieldSkill, 4);
        character.addEquipment(mediumShield);

        characterShield = character.getShield("Medium Shield");
    }

    @After
    public void tearDown() throws Exception {
        characterShield = null;
    }

    @Test
    public void getLabel() {
        assertEquals("Medium Shield", characterShield.getLabel());
    }

    @Test
    public void getShield() {
        assertEquals("Medium Shield", characterShield.getShield().getName());
    }

    @Test
    public void isPrimary() {
        assertTrue(characterShield.isPrimary());
    }
}