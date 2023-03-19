package norman.gurps.combat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.Armor;
import norman.gurps.combat.model.GameChar;
import norman.gurps.combat.model.Location;
import norman.gurps.combat.model.MeleeWeapon;
import norman.gurps.combat.model.MeleeWeaponMode;
import norman.gurps.combat.model.ParryType;
import norman.gurps.combat.model.Shield;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GameCharService {
    private static Logger LOGGER = LoggerFactory.getLogger(GameCharService.class);
    @Value("${storage.dir.name}")
    private String storageDirName;
    @Value("${storage.game.char.file.name}")
    private String storageGameCharFileName;
    private ObjectMapper mapper;
    private File storageDir;
    private File storageGameCharFile;

    public GameCharService(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @PostConstruct
    private void postConstruct() {
        storageDir = new File(SystemUtils.USER_HOME, storageDirName);
        storageGameCharFile = new File(storageDir, storageGameCharFileName);
    }

    public List<String> validate(GameChar gameChar) {
        List<String> errors = new ArrayList<>();

        if (StringUtils.isBlank(gameChar.getName())) {
            errors.add("Name may not be blank.");
        }

        if (gameChar.getStrength() == null) {
            errors.add("Strength may not be blank.");
        } else if (gameChar.getStrength() < 0) {
            errors.add("Strength may not be less than zero.");
        }

        if (gameChar.getDexterity() == null) {
            errors.add("Dexterity may not be blank.");
        } else if (gameChar.getDexterity() < 0) {
            errors.add("Dexterity may not be less than zero.");
        }

        if (gameChar.getIntelligence() == null) {
            errors.add("Intelligence may not be blank.");
        } else if (gameChar.getIntelligence() < 0) {
            errors.add("Intelligence may not be less than zero.");
        }

        if (gameChar.getHealth() == null) {
            errors.add("Health may not be blank.");
        } else if (gameChar.getHealth() < 0) {
            errors.add("Health may not be less than zero.");
        }

        if (gameChar.getHitPoints() == null) {
            errors.add("Hit points may not be blank.");
        } else if (gameChar.getHitPoints() < 0) {
            errors.add("Hit points may not be less than zero.");
        }

        if (gameChar.getBasicSpeed() == null) {
            errors.add("Basic speed may not be blank.");
        } else if (gameChar.getBasicSpeed() < 0) {
            errors.add("Basic speed may not be less than zero.");
        }

        if (gameChar.getBasicMove() == null) {
            errors.add("Basic move may not be blank.");
        } else if (gameChar.getBasicMove() < 0) {
            errors.add("Basic move may not be less than zero.");
        }

        if (gameChar.getEncumbranceLevel() == null) {
            errors.add("Encumbrance level may not be blank.");
        } else if (gameChar.getEncumbranceLevel() < 0 || gameChar.getEncumbranceLevel() > 4) {
            errors.add("Encumbrance level must be between 0 and 4.");
        }

        if (gameChar.getUnconsciousnessCheck() == null) {
            errors.add("Unconsciousness check may not be blank.");
        } else if (gameChar.getUnconsciousnessCheck() < 0) {
            errors.add("Unconsciousness check may not be less than 0.");
        }

        if (gameChar.getDeathCheck() == null) {
            errors.add("Death check may not be blank.");
        } else if (gameChar.getDeathCheck() < 0) {
            errors.add("Death check may not be less than 0.");
        }

        if (gameChar.getMeleeWeapons().isEmpty()) {
            errors.add("Must have at least one weapon.");
        } else {
            Set<String> weaponNames = new HashSet<>();
            for (MeleeWeapon weapon : gameChar.getMeleeWeapons()) {
                errors.addAll(validateMeleeWeapon(weapon, weaponNames));
            }
        }

        if (gameChar.getShields().isEmpty()) {
            Set<String> shieldNames = new HashSet<>();
            for (Shield shield : gameChar.getShields()) {
                errors.addAll(validateShield(shield, shieldNames));
            }
        }

        Set<Location> locations = new HashSet<>();
        for (Armor armor : gameChar.getArmorList()) {
            errors.addAll(validateArmor(armor, locations));
        }

        return errors;
    }

    private List<String> validateMeleeWeapon(MeleeWeapon weapon, Set<String> weaponNames) {
        List<String> errors = new ArrayList<>();

        if (StringUtils.isBlank(weapon.getName())) {
            errors.add("Weapon name may not be blank.");
        } else {
            if (weaponNames.contains(weapon.getName())) {
                errors.add("Weapon name " + weapon.getName() + " is not unique.");
            }
            weaponNames.add(weapon.getName());
        }

        if (weapon.getSkill() == null) {
            errors.add("Skill for weapon " + weapon.getName() + " may not be blank.");
        } else if (weapon.getSkill() < 0) {
            errors.add("Skill for weapon " + weapon.getName() + " may not be less than zero.");
        }

        if (weapon.getModes().isEmpty()) {
            errors.add("Weapon " + weapon.getName() + " must have at least one mode.");
        } else {
            Set<String> modeNames = new HashSet<>();
            for (MeleeWeaponMode mode : weapon.getModes()) {
                String weaponName = weapon.getName();
                errors.addAll(validateMeleeWeaponMode(mode, modeNames, weaponName));
            }
        }

        if (weapon.getParryType() == null) {
            errors.add("Parry type for weapon " + weapon.getName() + " may not be blank.");
        } else if (weapon.getParryType() != ParryType.NO) {
            if (weapon.getParryModifier() == null) {
                errors.add("Parry modifier for weapon " + weapon.getName() + " may not be blank.");
            }
        } else {
            if (weapon.getParryModifier() != null) {
                errors.add("Parry modifier for weapon " + weapon.getName() + " must be blank.");
            }
        }

        if (weapon.getMinStrength() == null) {
            errors.add("Minimum strength for weapon " + weapon.getName() + " may not be blank.");
        } else if (weapon.getMinStrength() < 0) {
            errors.add("Minimum strength for weapon " + weapon.getName() + " may not be less than zero.");
        }

        if (weapon.getTwoHanded() == null) {
            errors.add("Two-handed indicator for weapon " + weapon.getName() + " may not be blank.");
        }

        return errors;
    }

    private List<String> validateMeleeWeaponMode(MeleeWeaponMode mode, Set<String> modeNames, String weaponName) {
        List<String> errors = new ArrayList<>();

        if (StringUtils.isBlank(mode.getName())) {
            errors.add("Mode name for weapon " + weaponName + " may not be blank.");
        } else {
            if (modeNames.contains(mode.getName())) {
                errors.add("Mode name " + mode.getName() + " for weapon " + weaponName + " is not unique.");
            }
            modeNames.add(mode.getName());
        }

        if (mode.getDamageDice() == null) {
            errors.add("Damage dice for weapon & mode " + weaponName + "/" + mode.getName() + " may not be blank.");
        } else if (mode.getDamageDice() < 0) {
            errors.add("Damage dice for weapon & mode " + weaponName + "/" + mode.getName() +
                    " may not be less than zero.");
        }

        if (mode.getDamageAdds() == null) {
            errors.add("Damage adds for weapon & mode " + weaponName + "/" + mode.getName() + " may not be blank.");
        }

        if (mode.getDamageType() == null) {
            errors.add("Damage type for weapon & mode " + weaponName + "/" + mode.getName() + " may not be blank.");
        }

        if (mode.getReaches().isEmpty()) {
            errors.add("Weapon & mode " + weaponName + "/" + mode.getName() + " must have at least one reach.");
        } else {
            for (Integer reach : mode.getReaches()) {
                if (reach < 0) {
                    errors.add("All reaches for weapon & mode " + weaponName + "/" + mode.getName() +
                            " must be zero or greater.");
                    break;
                }
            }
        }

        return errors;
    }

    private List<String> validateShield(Shield shield, Set<String> shieldNames) {
        List<String> errors = new ArrayList<>();

        if (StringUtils.isBlank(shield.getName())) {
            errors.add("Shield name may not be blank.");
        } else {
            if (shieldNames.contains(shield.getName())) {
                errors.add("Shield name " + shield.getName() + " is not unique.");
            }
            shieldNames.add(shield.getName());
        }

        if (shield.getSkill() == null) {
            errors.add("Shield skill may not be blank.");
        } else if (shield.getSkill() < 0) {
            errors.add("Shield skill may not be less than zero.");
        }

        if (shield.getDefenseBonus() == null) {
            errors.add("Shield defense bonus may not be blank.");
        } else if (shield.getDefenseBonus() < 0) {
            errors.add("Shield defense bonus may not be less than zero.");
        }

        return errors;
    }

    private List<String> validateArmor(Armor armor, Set<Location> armorLocations) {
        List<String> errors = new ArrayList<>();

        if (armor.getLocation() == null) {
            errors.add("Armor location may not be blank.");
        } else {
            if (armorLocations.contains(armor.getLocation())) {
                errors.add("Armor location " + armor.getLocation() + " is not unique.");
            }
            armorLocations.add(armor.getLocation());
        }

        if (armor.getDamageResistance() == null) {
            errors.add("Damage resistance for armor location " + armor.getLocation() + " may not be blank.");
        } else if (armor.getDamageResistance() < 0) {
            errors.add("Damage resistance for armor location " + armor.getLocation() + " may not be less than zero.");
        }

        return errors;
    }

    public void storeChar(GameChar newGameChar) {
        // Verify game char has a good name.
        if (StringUtils.isBlank(newGameChar.getName())) {
            throw new LoggingException(LOGGER, "Invalid character. Name may not be blank.");
        }

        List<GameChar> gameChars = getStoredGameChars();

        // Verify another character with the same name does not already exist.
        Set<String> gameCharNames = new HashSet<>();
        for (GameChar gameChar : gameChars) {
            gameCharNames.add(gameChar.getName());
        }
        if (gameCharNames.contains(newGameChar.getName())) {
            throw new LoggingException(LOGGER,
                    "Unable to save character. Name " + newGameChar.getName() + " already exists.");
        }

        gameChars.add(newGameChar);
        saveStoredGameChars(gameChars);
    }

    public void removeChar(String name) {
        // Verify a good game char name was passed in.
        if (StringUtils.isBlank(name)) {
            throw new LoggingException(LOGGER, "Invalid character. Name may not be blank.");
        }

        // Find game char to remove.
        List<GameChar> gameChars = getStoredGameChars();
        GameChar foundGameChar = null;
        for (GameChar gameChar : gameChars) {
            if (gameChar.getName().equals(name)) {
                foundGameChar = gameChar;
            }
        }

        if (foundGameChar == null) {
            throw new LoggingException(LOGGER, "Unable to delete character. Name " + name + " does not exist.");
        }

        gameChars.remove(foundGameChar);
        saveStoredGameChars(gameChars);
    }

    public List<GameChar> getStoredGameChars() {
        // Create home directory if it does not exist.
        if (!storageDir.exists()) {
            LOGGER.debug("Creating storage directory " + storageDir + ".");
            if (!storageDir.mkdirs()) {
                throw new LoggingException(LOGGER, "Unable to create storage directory " + storageDir + ".");
            }
        }

        // Load stored chars file. Create it if it does not already exist.
        List<GameChar> gameChars = new ArrayList<>();
        if (storageGameCharFile.exists()) {
            LOGGER.debug("Loading stored characters.");
            try {
                GameChar[] gameCharArray = mapper.readValue(storageGameCharFile, GameChar[].class);
                gameChars.addAll(Arrays.asList(gameCharArray));
            } catch (IOException e) {
                throw new LoggingException(LOGGER,
                        "Error loading stored characters file from " + storageGameCharFile + ".", e);
            }
        } else {
            LOGGER.debug("Saving new stored characters file.");
            saveStoredGameChars(gameChars);
        }

        return gameChars;
    }

    private void saveStoredGameChars(List<GameChar> gameChars) {
        LOGGER.debug("Storing characters to local storage.");
        try {
            mapper.writeValue(storageGameCharFile, gameChars);
        } catch (IOException e) {
            throw new LoggingException(LOGGER, "Error storing characters to file " + storageGameCharFile + ".", e);
        }
    }
}