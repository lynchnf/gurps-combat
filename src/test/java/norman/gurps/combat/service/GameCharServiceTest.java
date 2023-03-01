package norman.gurps.combat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.combat.model.Armor;
import norman.gurps.combat.model.DamageType;
import norman.gurps.combat.model.GameChar;
import norman.gurps.combat.model.Location;
import norman.gurps.combat.model.MeleeWeapon;
import norman.gurps.combat.model.MeleeWeaponMode;
import norman.gurps.combat.model.ParryType;
import norman.gurps.combat.model.Shield;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameCharServiceTest {
    GameCharService service;
    File tempDir;
    File tempFile;
    GameChar testGameChar;

    @BeforeEach
    void setUp() throws Exception {
        service = new GameCharService(new ObjectMapper());

        // Override storage directory for testing.
        tempDir = Files.createTempDirectory("gurps-combat-temp-").toFile();
        ReflectionTestUtils.setField(service, "storageDir", tempDir);

        // Override storage file for testing.
        tempFile = new File(tempDir, "game-char.json");
        ReflectionTestUtils.setField(service, "storageGameCharFile", tempFile);

        testGameChar = new GameChar();
        testGameChar.setName("Test Character");
        testGameChar.setStrength(14);
        testGameChar.setDexterity(13);
        testGameChar.setIntelligence(12);
        testGameChar.setHealth(11);
        testGameChar.setHitPoints(15);
        testGameChar.setBasicSpeed(6.25);
        MeleeWeapon weapon = new MeleeWeapon();
        weapon.setName("Broadsword");
        weapon.setSkill(13);
        MeleeWeaponMode swing = new MeleeWeaponMode();
        swing.setName("swing");
        swing.setDamageDice(2);
        swing.setDamageAdds(1);
        swing.setDamageType(DamageType.CUTTING);
        swing.getReaches().add(1);
        swing.setParryType(ParryType.YES);
        swing.setParryModifier(0);
        weapon.getModes().add(swing);
        MeleeWeaponMode thrust = new MeleeWeaponMode();
        thrust.setName("thrust");
        thrust.setDamageDice(1);
        thrust.setDamageAdds(1);
        thrust.setDamageType(DamageType.CRUSHING);
        thrust.getReaches().add(1);
        thrust.setParryType(ParryType.YES);
        thrust.setParryModifier(0);
        weapon.getModes().add(thrust);
        weapon.setMinStrength(10);
        testGameChar.getMeleeWeapons().add(weapon);
        Shield shield = new Shield();
        shield.setName("Medium Shield");
        shield.setSkill(13);
        shield.setDefenseBonus(2);
        testGameChar.setShield(shield);
        Armor torso = new Armor();
        torso.setLocation(Location.TORSO);
        torso.setDamageResistance(2);
        testGameChar.getArmorList().add(torso);
        Armor groin = new Armor();
        groin.setLocation(Location.GROIN);
        groin.setDamageResistance(2);
        testGameChar.getArmorList().add(groin);
        Armor legs = new Armor();
        legs.setLocation(Location.LEGS);
        legs.setDamageResistance(2);
        testGameChar.getArmorList().add(legs);
        Armor arms = new Armor();
        arms.setLocation(Location.ARMS);
        arms.setDamageResistance(2);
        testGameChar.getArmorList().add(arms);
        Armor skull = new Armor();
        skull.setLocation(Location.SKULL);
        skull.setDamageResistance(2);
        testGameChar.getArmorList().add(skull);
        Armor face = new Armor();
        face.setLocation(Location.FACE);
        face.setDamageResistance(2);
        testGameChar.getArmorList().add(face);
        Armor hands = new Armor();
        hands.setLocation(Location.HANDS);
        hands.setDamageResistance(2);
        testGameChar.getArmorList().add(hands);
        Armor feet = new Armor();
        feet.setLocation(Location.FEET);
        feet.setDamageResistance(2);
        testGameChar.getArmorList().add(feet);
    }

    @Test
    void validate() {
        List<String> errors = service.validate(testGameChar);

        assertEquals(0, errors.size());
    }

    @Test
    void storeChar() throws Exception {
        service.storeChar(testGameChar);

        // Validate game character was written to local storage.
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(tempDir, "game-char.json");
        GameChar[] gameChars = mapper.readValue(file, GameChar[].class);
        assertEquals(1, gameChars.length);
        assertEquals("Test Character", gameChars[0].getName());
    }

    @Test
    void removeChar() throws Exception {
        // Preload local storage.
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(tempDir, "game-char.json");
        List<GameChar> gameCharList = new ArrayList<>();
        gameCharList.add(testGameChar);
        mapper.writeValue(file, gameCharList);

        service.removeChar("Test Character");

        // Validate game character was removed from to local storage.
        GameChar[] gameChars = mapper.readValue(file, GameChar[].class);
        assertEquals(0, gameChars.length);
    }

    @Test
    void getStoredGameChars() throws Exception {
        // Preload local storage.
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(tempDir, "game-char.json");
        List<GameChar> gameCharList = new ArrayList<>();
        gameCharList.add(testGameChar);
        mapper.writeValue(file, gameCharList);

        List<GameChar> gameChars = service.getStoredGameChars();

        assertEquals(1, gameChars.size());
        assertEquals("Test Character", gameChars.get(0).getName());
    }
}