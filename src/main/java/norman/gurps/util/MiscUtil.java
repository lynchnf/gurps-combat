package norman.gurps.util;

import norman.gurps.skill.ControllingAttribute;
import norman.gurps.skill.DifficultyLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class MiscUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(MiscUtil.class);
    private static final Random RANDOM = new Random();

    public static int rollDice(int nbrOfDice) {
        return rollDice(nbrOfDice, 0);
    }

    public static int rollDice(int nbrOfDice, int adds) {
        int total = 0;
        for (int i = 0; i < nbrOfDice; i++) {
            total += RANDOM.nextInt(6) + 1;
        }
        return total + adds;
    }

    public static RollStatus calculateStatus(int effectiveSkill, int roll) {
        if (roll == 3 || roll == 4 || roll == 5 && effectiveSkill >= 15 || roll == 6 && effectiveSkill >= 16) {
            return RollStatus.CRITICAL_SUCCESS;
        } else if (roll == 18 || roll == 17 && effectiveSkill <= 15 || roll >= effectiveSkill + 10) {
            return RollStatus.CRITICAL_FAILURE;
        } else if (roll == 17 && effectiveSkill > 15) {
            return RollStatus.FAILURE;
        } else if (roll <= effectiveSkill) {
            return RollStatus.SUCCESS;
        } else {
            return RollStatus.FAILURE;
        }
    }

    public static RollStatus calculateSimpleStatus(int effectiveSkill, int roll) {
        RollStatus rollStatus = calculateStatus(effectiveSkill, roll);
        if (rollStatus == RollStatus.CRITICAL_SUCCESS || rollStatus == RollStatus.SUCCESS) {
            return RollStatus.SUCCESS;
        } else {
            return RollStatus.FAILURE;
        }
    }

    public static int calculateSkillLevel(ControllingAttribute controllingAttribute, DifficultyLevel difficultyLevel,
            int points, int strength, int dexterity, int intelligence, int health) {
        int attribute = 0;
        if (controllingAttribute == ControllingAttribute.ST) {
            attribute = strength;
        } else if (controllingAttribute == ControllingAttribute.DX) {
            attribute = dexterity;
        } else if (controllingAttribute == ControllingAttribute.IQ) {
            attribute = intelligence;
        } else if (controllingAttribute == ControllingAttribute.HT) {
            attribute = health;
        } else {
            LOGGER.error("Invalid value for controllingAttribute: \"" + controllingAttribute + "\"");
        }
        return calculateSkillLevel(attribute, difficultyLevel, points);
    }

    public static int calculateSkillLevel(int attribute, DifficultyLevel difficultyLevel, int points) {
        int level = Math.min(20, attribute);

        if (difficultyLevel == DifficultyLevel.EASY) {
            // do nothing.
        } else if (difficultyLevel == DifficultyLevel.AVERAGE) {
            level -= 1;
        } else if (difficultyLevel == DifficultyLevel.HARD) {
            level -= 2;
        } else {
            LOGGER.error("Invalid value for difficultyLevel: \"" + difficultyLevel + "\"");
        }

        if (points >= 4) {
            level += points / 4 + 1;
        } else if (points >= 2) {
            level += 1;
        } else if (points < 1) {
            level -= 4;
        }
        return level;
    }
}
