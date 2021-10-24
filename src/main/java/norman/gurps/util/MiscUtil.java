package norman.gurps.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.LoggingException;
import norman.gurps.equipment.Armor;
import norman.gurps.equipment.Shield;
import norman.gurps.equipment.Weapon;
import norman.gurps.skill.ControllingAttribute;
import norman.gurps.skill.DifficultyLevel;
import norman.gurps.skill.Skill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MiscUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(MiscUtil.class);
    private static Random random;
    private static ClassLoader loader = Thread.currentThread().getContextClassLoader();
    private static ObjectMapper mapper = new ObjectMapper();
    private static String dataDirectory = "data";

    public static Random getRandom() {
        if (random == null) {
            random = new Random();
        }
        return random;
    }

    public static void setRandom(Random random) {
        MiscUtil.random = random;
    }

    public static int rollDice(int nbrOfDice) {
        return rollDice(nbrOfDice, 0);
    }

    public static int rollDice(int nbrOfDice, int adds) {
        int total = 0;
        for (int i = 0; i < nbrOfDice; i++) {
            total += getRandom().nextInt(6) + 1;
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

    public static Map<String, Armor> getArmors() {
        String fileName = "armors.json";
        Class<?> clazz = Armor.class;
        InputStream stream = loader.getResourceAsStream(dataDirectory + File.separator + fileName);
        try {
            Armor[] thingArray = mapper.readValue(stream, Armor[].class);
            Map<String, Armor> thingMap = new HashMap<>();
            for (Armor thing : thingArray) {
                String name = thing.getName();
                thingMap.put(name, thing);
            }
            return thingMap;
        } catch (IOException e) {
            throw new LoggingException(LOGGER,
                    String.format("Unable to create %s objects from file %s.", clazz.getSimpleName(), fileName), e);
        }
    }

    public static Map<String, Shield> getShields() {
        String fileName = "shields.json";
        Class<?> clazz = Shield.class;
        InputStream stream = loader.getResourceAsStream(dataDirectory + File.separator + fileName);
        try {
            Shield[] thingArray = mapper.readValue(stream, Shield[].class);
            Map<String, Shield> thingMap = new HashMap<>();
            for (Shield thing : thingArray) {
                String name = thing.getName();
                thingMap.put(name, thing);
            }
            return thingMap;
        } catch (IOException e) {
            throw new LoggingException(LOGGER,
                    String.format("Unable to create %s objects from file %s.", clazz.getSimpleName(), fileName), e);
        }
    }

    public static Map<String, Skill> getSkills() {
        String fileName = "skills.json";
        Class<?> clazz = Skill.class;
        InputStream stream = loader.getResourceAsStream(dataDirectory + File.separator + fileName);
        try {
            Skill[] thingArray = mapper.readValue(stream, Skill[].class);
            Map<String, Skill> thingMap = new HashMap<>();
            for (Skill thing : thingArray) {
                String name = thing.getName();
                thingMap.put(name, thing);
            }
            return thingMap;
        } catch (IOException e) {
            throw new LoggingException(LOGGER,
                    String.format("Unable to create %s objects from file %s.", clazz.getSimpleName(), fileName), e);
        }
    }

    public static Map<String, Weapon> getWeapons() {
        String fileName = "weapons.json";
        Class<?> clazz = Weapon.class;
        InputStream stream = loader.getResourceAsStream(dataDirectory + File.separator + fileName);
        try {
            Weapon[] thingArray = mapper.readValue(stream, Weapon[].class);
            Map<String, Weapon> thingMap = new HashMap<>();
            for (Weapon thing : thingArray) {
                String name = thing.getName();
                thingMap.put(name, thing);
            }
            return thingMap;
        } catch (IOException e) {
            throw new LoggingException(LOGGER,
                    String.format("Unable to create %s objects from file %s.", clazz.getSimpleName(), fileName), e);
        }
    }
}
