package norman.gurps.character;

import norman.gurps.LoggingException;
import norman.gurps.skill.Skill;
import norman.gurps.util.MiscUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharacterSkill {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharacterSkill.class);
    private GameCharacter character;
    private String label;
    private Skill skill;
    private int points;

    public CharacterSkill(GameCharacter character, String label, Skill skill, int points) {
        if (points != 0 && points != 1 && points != 2 && (points < 4 || points % 4 != 0)) {
            throw new LoggingException(LOGGER, "Skill Points must be 0, 1, 2, or a multiple of 4");
        }
        this.character = character;
        this.label = label;
        this.skill = skill;
        this.points = points;
    }

    public int getLevel() {
        return MiscUtil.calculateSkillLevel(skill.getControllingAttribute(), skill.getDifficultyLevel(), points,
                character.getStrength(), character.getDexterity(), character.getIntelligence(), character.getHealth());
    }

    public String getLabel() {
        return label;
    }

    public Skill getSkill() {
        return skill;
    }

    public int getPoints() {
        return points;
    }
}
