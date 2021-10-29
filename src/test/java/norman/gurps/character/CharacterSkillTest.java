package norman.gurps.character;

import norman.gurps.skill.ControllingAttribute;
import norman.gurps.skill.DifficultyLevel;
import norman.gurps.skill.Skill;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CharacterSkillTest {
    CharacterSkill characterSkill;

    @BeforeEach
    void setUp() {
        Skill broadswordSkill = new Skill();
        broadswordSkill.setName("Broadsword");
        broadswordSkill.setControllingAttribute(ControllingAttribute.DX);
        broadswordSkill.setDifficultyLevel(DifficultyLevel.AVERAGE);

        GameCharacter character = new GameCharacter();
        character.setName("Testy McTester");
        character.setStrength(9);
        character.setDexterity(13);
        character.setIntelligence(12);
        character.setHealth(14);
        character.addSkill(broadswordSkill, 15);

        characterSkill = character.getSkill(broadswordSkill.getName());
    }

    @AfterEach
    void tearDown() {
        characterSkill = null;
    }

    @Test
    void getLabel() {
        assertEquals("Broadsword", characterSkill.getLabel());
    }

    @Test
    void getSkill() {
        assertEquals("Broadsword", characterSkill.getSkill().getName());
    }

    @Test
    void getLevel() {
        assertEquals(15, characterSkill.getLevel());
    }
}