package norman.gurps.character;

import norman.gurps.skill.Skill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Read-only bean that contains skills plus additional properties specific to a character.
 */
public class CharacterSkill {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterSkill.class);
    private GameCharacter character;
    private String label;
    private Skill skill;
    private int level;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //  Constructors, Getters, and Setters /////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public CharacterSkill(GameCharacter character, String label, Skill skill, int level) {
        this.character = character;
        this.label = label;
        this.skill = skill;
        this.level = level;
    }

    public String getLabel() {
        return label;
    }

    public Skill getSkill() {
        return skill;
    }

    public int getLevel() {
        return level;
    }
}
