package norman.gurps.character;

import norman.gurps.skill.ControllingAttribute;
import norman.gurps.skill.DifficultyLevel;
import norman.gurps.skill.Skill;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CharacterSkillTest {
    private CharacterSkill characterSkill;

    @Before
    public void setUp() throws Exception {
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
        character.addSkill(broadswordSkill, 1);

        characterSkill = character.getSkill(broadswordSkill.getName());
    }

    @After
    public void tearDown() throws Exception {
        characterSkill = null;
    }

    @Test
    public void getLevel() {
        assertEquals(12, characterSkill.getLevel());
    }

    @Test
    public void getLabel() {
        assertEquals("Broadsword", characterSkill.getLabel());
    }

    @Test
    public void getSkill() {
        assertEquals("Broadsword", characterSkill.getSkill().getName());
    }

    @Test
    public void getPoints() {
        assertEquals(1, characterSkill.getPoints());
    }
}
