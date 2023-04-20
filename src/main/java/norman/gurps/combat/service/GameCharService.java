package norman.gurps.combat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.ArmorPiece;
import norman.gurps.combat.model.GameChar;
import norman.gurps.combat.model.HitLocation;
import norman.gurps.combat.model.MeleeWeapon;
import norman.gurps.combat.model.MeleeWeaponMode;
import norman.gurps.combat.model.ParryType;
import norman.gurps.combat.model.RangedWeapon;
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

        if (gameChar.getDamageResistance() == null) {
            errors.add("Natural damage resistance may not be blank.");
        } else if (gameChar.getDamageResistance() < 0) {
            errors.add("Natural damage resistance may not be less than zero.");
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

        Set<String> itemNames = new HashSet<>();
        for (MeleeWeapon meleeWeapon : gameChar.getMeleeWeapons()) {
            errors.addAll(validateMeleeWeapon(meleeWeapon, itemNames));
        }

        for (RangedWeapon rangedWeapon : gameChar.getRangedWeapons()) {
            errors.addAll(validateRangedWeapon(rangedWeapon, itemNames));
        }

        for (Shield shield : gameChar.getShields()) {
            errors.addAll(validateShield(shield, itemNames));
        }

        Set<String> armorPieceNames = new HashSet<>();
        for (ArmorPiece armorPiece : gameChar.getArmorPieces()) {
            errors.addAll(validateArmor(armorPiece, armorPieceNames));
        }

        List<String> defaultReadyItems = gameChar.getDefaultReadyItems();
        for (String defaultReadyItem : defaultReadyItems) {
            if (!itemNames.contains(defaultReadyItem)) {
                errors.add("Default ready item " + defaultReadyItem +
                        " is not a valid melee weapon, nor a valid ranged weapon, nor a valid shield.");
            }
        }

        return errors;
    }

    private List<String> validateMeleeWeapon(MeleeWeapon meleeWeapon, Set<String> itemNames) {
        List<String> errors = new ArrayList<>();

        if (StringUtils.isBlank(meleeWeapon.getName())) {
            errors.add("Melee weapon name may not be blank.");
        } else {
            if (itemNames.contains(meleeWeapon.getName())) {
                errors.add("Melee weapon name " + meleeWeapon.getName() + " is not unique.");
            }
            itemNames.add(meleeWeapon.getName());
        }

        if (meleeWeapon.getSkill() == null) {
            errors.add("Skill for melee weapon " + meleeWeapon.getName() + " may not be blank.");
        } else if (meleeWeapon.getSkill() < 0) {
            errors.add("Skill for melee weapon " + meleeWeapon.getName() + " may not be less than zero.");
        }

        if (meleeWeapon.getMeleeWeaponModes().isEmpty()) {
            errors.add("Melee weapon " + meleeWeapon.getName() + " must have at least one melee weapon mode.");
        } else {
            Set<String> meleeModeNames = new HashSet<>();
            for (MeleeWeaponMode meleeWeaponMode : meleeWeapon.getMeleeWeaponModes()) {
                errors.addAll(validateMeleeWeaponMode(meleeWeaponMode, meleeModeNames, meleeWeapon.getName()));
            }
        }

        if (meleeWeapon.getParryType() == null) {
            errors.add("Parry type for melee weapon " + meleeWeapon.getName() + " may not be blank.");
        } else if (meleeWeapon.getParryType() != ParryType.NO) {
            if (meleeWeapon.getParryModifier() == null) {
                errors.add("Parry modifier for melee weapon " + meleeWeapon.getName() + " may not be blank.");
            }
        } else {
            if (meleeWeapon.getParryModifier() != null) {
                errors.add("Parry modifier for melee weapon " + meleeWeapon.getName() + " must be blank.");
            }
        }

        if (meleeWeapon.getMinimumStrength() == null) {
            errors.add("Minimum strength for melee weapon " + meleeWeapon.getName() + " may not be blank.");
        } else if (meleeWeapon.getMinimumStrength() < 0) {
            errors.add("Minimum strength for melee weapon " + meleeWeapon.getName() + " may not be less than zero.");
        }

        return errors;
    }

    private List<String> validateRangedWeapon(RangedWeapon rangedWeapon, Set<String> itemNames) {
        List<String> errors = new ArrayList<>();

        if (StringUtils.isBlank(rangedWeapon.getName())) {
            errors.add("Ranged weapon name may not be blank.");
        } else {
            if (itemNames.contains(rangedWeapon.getName())) {
                errors.add("Ranged weapon name " + rangedWeapon.getName() + " is not unique.");
            }
            itemNames.add(rangedWeapon.getName());
        }

        if (rangedWeapon.getSkill() == null) {
            errors.add("Skill for ranged weapon " + rangedWeapon.getName() + " may not be blank.");
        } else if (rangedWeapon.getSkill() < 0) {
            errors.add("Skill for ranged weapon " + rangedWeapon.getName() + " may not be less than zero.");
        }

        if (rangedWeapon.getDamageDice() == null) {
            errors.add("Damage dice for ranged weapon " + rangedWeapon.getName() + " may not be blank.");
        } else if (rangedWeapon.getDamageDice() < 0) {
            errors.add("Damage dice for ranged weapon " + rangedWeapon.getName() + " may not be less than zero.");
        }

        if (rangedWeapon.getDamageAdds() == null) {
            errors.add("Damage adds for ranged weapon " + rangedWeapon.getName() + " may not be blank.");
        }

        if (rangedWeapon.getDamageType() == null) {
            errors.add("Damage type for ranged weapon " + rangedWeapon.getName() + " may not be blank.");
        }

        if (rangedWeapon.getAccuracy() == null) {
            errors.add("Accuracy for ranged weapon " + rangedWeapon.getName() + " may not be blank.");
        } else if (rangedWeapon.getAccuracy() < 0) {
            errors.add("Accuracy for ranged weapon " + rangedWeapon.getName() + " may not be less than zero.");
        }

        if (rangedWeapon.getHalfDamageRange() != null && rangedWeapon.getHalfDamageRange() <= 0) {
            errors.add("Half damage range for ranged weapon " + rangedWeapon.getName() +
                    " must be more than zero, if it's not blank.");
        }

        if (rangedWeapon.getMaximumRange() == null) {
            errors.add("Maximum range for ranged weapon " + rangedWeapon.getName() + " may not be blank.");
        } else if (rangedWeapon.getMaximumRange() <= 0) {
            errors.add("Maximum range for ranged weapon " + rangedWeapon.getName() + " must be more than zero.");
        }
        if (rangedWeapon.getRateOfFire() == null) {
            errors.add("Rate of fire for ranged weapon " + rangedWeapon.getName() + " may not be blank.");
        } else if (rangedWeapon.getRateOfFire() <= 0) {
            errors.add("Rate of fire for ranged weapon " + rangedWeapon.getName() + " must be more than zero.");
        }

        if (rangedWeapon.getMinimumStrength() == null) {
            errors.add("Minimum strength for ranged weapon " + rangedWeapon.getName() + " may not be blank.");
        } else if (rangedWeapon.getMinimumStrength() < 0) {
            errors.add("Minimum strength for ranged weapon " + rangedWeapon.getName() + " may not be less than zero.");
        }

        if (rangedWeapon.getBulk() == null) {
            errors.add("Bulk for ranged weapon " + rangedWeapon.getName() + " may not be blank.");
        } else if (rangedWeapon.getBulk() >= 0) {
            errors.add("Bulk for ranged weapon " + rangedWeapon.getName() + " must be less than zero.");
        }

        if (rangedWeapon.getRecoil() == null) {
            errors.add("Recoil for ranged weapon " + rangedWeapon.getName() + " may not be blank.");
        } else if (rangedWeapon.getRecoil() <= 0) {
            errors.add("Recoil for ranged weapon " + rangedWeapon.getName() + " must be more than zero.");
        }

        return errors;
    }

    private List<String> validateMeleeWeaponMode(MeleeWeaponMode meleeWeaponMode, Set<String> meleeModeNames,
            String meleeWeaponName) {
        List<String> errors = new ArrayList<>();

        if (StringUtils.isBlank(meleeWeaponMode.getName())) {
            errors.add("Melee weapon mode name for melee weapon " + meleeWeaponName + " may not be blank.");
        } else {
            if (meleeModeNames.contains(meleeWeaponMode.getName())) {
                errors.add(
                        "Melee weapon mode name " + meleeWeaponMode.getName() + " for melee weapon " + meleeWeaponName +
                                " is not unique.");
            }
            meleeModeNames.add(meleeWeaponMode.getName());
        }

        if (meleeWeaponMode.getDamageDice() == null) {
            errors.add("Damage dice for melee weapon & mode " + meleeWeaponName + "/" + meleeWeaponMode.getName() +
                    " may not be blank.");
        } else if (meleeWeaponMode.getDamageDice() < 0) {
            errors.add("Damage dice for melee weapon & mode " + meleeWeaponName + "/" + meleeWeaponMode.getName() +
                    " may not be less than zero.");
        }

        if (meleeWeaponMode.getDamageAdds() == null) {
            errors.add("Damage adds for melee weapon & mode " + meleeWeaponName + "/" + meleeWeaponMode.getName() +
                    " may not be blank.");
        }

        if (meleeWeaponMode.getDamageType() == null) {
            errors.add("Damage type for melee weapon & mode " + meleeWeaponName + "/" + meleeWeaponMode.getName() +
                    " may not be blank.");
        }

        if (meleeWeaponMode.getReaches().isEmpty()) {
            errors.add("Melee weapon & mode " + meleeWeaponName + "/" + meleeWeaponMode.getName() +
                    " must have at least one reach.");
        } else {
            Set<Integer> reachSet = new HashSet<>();
            for (Integer reach : meleeWeaponMode.getReaches()) {
                if (reach < 0) {
                    errors.add("Reach " + reach + " for melee weapon & mode " + meleeWeaponName + "/" +
                            meleeWeaponMode.getName() + " must be zero or greater.");
                } else if (reachSet.contains(reach)) {
                    errors.add("Reach " + reach + " for melee weapon & mode " + meleeWeaponName + "/" +
                            meleeWeaponMode.getName() + " is not unique.");
                }
                reachSet.add(reach);
            }
        }

        return errors;
    }

    private List<String> validateShield(Shield shield, Set<String> itemNames) {
        List<String> errors = new ArrayList<>();

        if (StringUtils.isBlank(shield.getName())) {
            errors.add("Shield name may not be blank.");
        } else {
            if (itemNames.contains(shield.getName())) {
                errors.add("Shield name " + shield.getName() + " is not unique.");
            }
            itemNames.add(shield.getName());
        }

        if (shield.getSkill() == null) {
            errors.add("Skill for shield " + shield.getName() + " may not be blank.");
        } else if (shield.getSkill() < 0) {
            errors.add("Skill for shield " + shield.getName() + " may not be less than zero.");
        }

        if (shield.getDefenseBonus() == null) {
            errors.add("Defense bonus for shield " + shield.getName() + " may not be blank.");
        } else if (shield.getDefenseBonus() < 0) {
            errors.add("Defense bonus for shield " + shield.getName() + " may not be less than zero.");
        }

        return errors;
    }

    private List<String> validateArmor(ArmorPiece armorPiece, Set<String> armorPieceNames) {
        List<String> errors = new ArrayList<>();

        if (StringUtils.isBlank(armorPiece.getName())) {
            errors.add("Armor piece name may not be blank.");
        } else {
            if (armorPieceNames.contains(armorPiece.getName())) {
                errors.add("Armor piece name " + armorPiece.getName() + " is not unique.");
            }
            armorPieceNames.add(armorPiece.getName());
        }

        if (armorPiece.getHitLocations().isEmpty()) {
            errors.add("Armor piece " + armorPiece.getName() + " must have at least one hit location.");
        } else {
            Set<HitLocation> hitLocationSet = new HashSet<>();
            for (HitLocation hitLocation : armorPiece.getHitLocations()) {
                if (hitLocationSet.contains(hitLocation)) {
                    errors.add("Hit location " + hitLocation + " for armor piece " + armorPiece.getName() +
                            " is not unique.");
                }
                hitLocationSet.add(hitLocation);
            }
        }

        if (armorPiece.getDamageResistance() == null) {
            errors.add("Damage resistance for armor piece " + armorPiece.getName() + " may not be blank.");
        } else if (armorPiece.getDamageResistance() < 0) {
            errors.add("Damage resistance for armor piece " + armorPiece.getName() + " may not be less than zero.");
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
