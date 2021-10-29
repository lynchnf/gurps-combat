package norman.gurps.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.LoggingException;
import norman.gurps.equipment.Armor;
import norman.gurps.equipment.Shield;
import norman.gurps.equipment.Weapon;
import norman.gurps.skill.Skill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MiscUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(MiscUtil.class);
    private static ClassLoader loader = Thread.currentThread().getContextClassLoader();
    private static ObjectMapper mapper = new ObjectMapper();
    private static String dataDirectory = "data";

    public static int getThrustDamageDice(int strength) {
        return strength < 11 ? 1 : (strength - 3) / 8;
    }

    public static int getThrustDamageAdds(int strength) {
        return strength < 11 ? (strength - 1) / 2 - 6 : (strength - 3) % 8 / 2 - 1;
    }

    public static int getSwingDamageDice(int strength) {
        return strength < 9 ? 1 : (strength - 5) / 4;
    }

    public static int getSwingDamageAdds(int strength) {
        return strength < 9 ? (strength - 1) / 2 - 5 : (strength - 5) % 4 - 1;
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
