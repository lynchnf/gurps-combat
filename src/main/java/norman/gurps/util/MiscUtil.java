package norman.gurps.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.LoggingException;
import norman.gurps.character.GameCharacter;
import norman.gurps.equipment.Armor;
import norman.gurps.equipment.Item;
import norman.gurps.equipment.Shield;
import norman.gurps.equipment.Weapon;
import norman.gurps.skill.Skill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiscUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(MiscUtil.class);
    private static ClassLoader loader = Thread.currentThread().getContextClassLoader();
    private static ObjectMapper mapper = new ObjectMapper();
    private static String dataDirectory = "data";
    private static Map<String, Skill> skills = null;
    private static Map<String, Item> items = null;
    private static Map<String, Armor> armors = null;
    private static Map<String, Shield> shields = null;
    private static Map<String, Weapon> weapons = null;

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

    public static Map<String, Skill> getSkills() {
        String fileName = "skills.json";
        Class<?> clazz = Skill.class;
        try {
            if (skills == null) {
                InputStream stream = loader.getResourceAsStream(dataDirectory + File.separator + fileName);
                Skill[] thingArray = mapper.readValue(stream, Skill[].class);
                Map<String, Skill> thingMap = new HashMap<>();
                for (Skill thing : thingArray) {
                    String name = thing.getName();
                    thingMap.put(name, thing);
                }
                skills = thingMap;
            }
            return skills;
        } catch (IOException e) {
            throw new LoggingException(LOGGER,
                    String.format("Unable to create %s objects from file %s.", clazz.getSimpleName(), fileName), e);
        }
    }

    public static Map<String, Item> getItems() {
        if (items == null) {
            items = new HashMap<>();
            items.putAll(getArmors());
            items.putAll(getShields());
            items.putAll(getWeapons());
        }
        return items;
    }

    public static Map<String, Armor> getArmors() {
        String fileName = "armors.json";
        Class<?> clazz = Armor.class;
        try {
            if (armors == null) {
                InputStream stream = loader.getResourceAsStream(dataDirectory + File.separator + fileName);
                Armor[] thingArray = mapper.readValue(stream, Armor[].class);
                Map<String, Armor> thingMap = new HashMap<>();
                for (Armor thing : thingArray) {
                    String name = thing.getName();
                    thingMap.put(name, thing);
                }
                armors = thingMap;
            }
            return armors;
        } catch (IOException e) {
            throw new LoggingException(LOGGER,
                    String.format("Unable to create %s objects from file %s.", clazz.getSimpleName(), fileName), e);
        }
    }

    public static Map<String, Shield> getShields() {
        String fileName = "shields.json";
        Class<?> clazz = Shield.class;
        try {
            if (shields == null) {
                InputStream stream = loader.getResourceAsStream(dataDirectory + File.separator + fileName);
                Shield[] thingArray = mapper.readValue(stream, Shield[].class);
                Map<String, Shield> thingMap = new HashMap<>();
                for (Shield thing : thingArray) {
                    String name = thing.getName();
                    thingMap.put(name, thing);
                }
                shields = thingMap;
            }
            return shields;
        } catch (IOException e) {
            throw new LoggingException(LOGGER,
                    String.format("Unable to create %s objects from file %s.", clazz.getSimpleName(), fileName), e);
        }
    }

    public static Map<String, Weapon> getWeapons() {
        String fileName = "weapons.json";
        Class<?> clazz = Weapon.class;
        try {
            if (weapons == null) {
                InputStream stream = loader.getResourceAsStream(dataDirectory + File.separator + fileName);
                Weapon[] thingArray = mapper.readValue(stream, Weapon[].class);
                Map<String, Weapon> thingMap = new HashMap<>();
                for (Weapon thing : thingArray) {
                    String name = thing.getName();
                    thingMap.put(name, thing);
                }
                weapons = thingMap;
            }
            return weapons;
        } catch (IOException e) {
            throw new LoggingException(LOGGER,
                    String.format("Unable to create %s objects from file %s.", clazz.getSimpleName(), fileName), e);
        }
    }

    public static GameCharacter convertJson(String filePath) {
        GameCharacter gameCharacter = new GameCharacter();
        File file = new File(filePath);
        try {
            Map<String, Object> jsonMap = mapper.readValue(file, Map.class);
            String name = (String) jsonMap.get("name");
            gameCharacter.setName(name);
            int strength = (int) jsonMap.get("strength");
            gameCharacter.setStrength(strength);
            int dexterity = (int) jsonMap.get("dexterity");
            gameCharacter.setDexterity(dexterity);
            int intelligence = (int) jsonMap.get("intelligence");
            gameCharacter.setIntelligence(intelligence);
            int health = (int) jsonMap.get("health");
            gameCharacter.setHealth(health);
            Map<String, Integer> characterSkills = (Map<String, Integer>) jsonMap.get("skills");
            for (String skillName : characterSkills.keySet()) {
                Skill skill = MiscUtil.getSkills().get(skillName);
                Integer level = characterSkills.get(skillName);
                gameCharacter.addSkill(skill, level);
            }
            List<String> equipment = (List<String>) jsonMap.get("equipment");
            for (String itemName : equipment) {
                Item item = MiscUtil.getItems().get(itemName);
                gameCharacter.addEquipment(item);
            }
            return gameCharacter;
        } catch (IOException e) {
            throw new LoggingException(LOGGER, String.format("Unable to read JSON file: %s", filePath), e);
        }
    }
}
