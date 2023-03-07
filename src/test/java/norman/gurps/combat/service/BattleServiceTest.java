package norman.gurps.combat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import norman.gurps.combat.model.Armor;
import norman.gurps.combat.model.Battle;
import norman.gurps.combat.model.BattleLog;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.DamageType;
import norman.gurps.combat.model.GameChar;
import norman.gurps.combat.model.Location;
import norman.gurps.combat.model.MeleeWeapon;
import norman.gurps.combat.model.MeleeWeaponMode;
import norman.gurps.combat.model.NextStep;
import norman.gurps.combat.model.ParryType;
import norman.gurps.combat.model.Phase;
import norman.gurps.combat.model.Shield;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BattleServiceTest {
    BattleService service;
    GameCharService gameCharService;
    File tempDir;
    File tempFile;
    GameChar testGameChar;

    @BeforeEach
    void setUp() throws Exception {
        gameCharService = mock(GameCharService.class);
        service = new BattleService(new ObjectMapper(), gameCharService);

        // Override storage directory for testing.
        tempDir = Files.createTempDirectory("gurps-combat-temp-").toFile();
        ReflectionTestUtils.setField(service, "storageDir", tempDir);

        // Override storage file for testing.
        tempFile = new File(tempDir, "battle.json");
        ReflectionTestUtils.setField(service, "storageBattleFile", tempFile);

        testGameChar = new GameChar();
        testGameChar.setName("Bob the Example");
        testGameChar.setStrength(14);
        testGameChar.setDexterity(13);
        testGameChar.setIntelligence(12);
        testGameChar.setHealth(11);
        testGameChar.setHitPoints(15);
        testGameChar.setBasicSpeed(6.25);
        testGameChar.setBasicMove(7);
        testGameChar.setEncumbranceLevel(1);
        MeleeWeapon weapon = new MeleeWeapon();
        weapon.setName("Broadsword");
        weapon.setSkill(14);
        MeleeWeaponMode swing = new MeleeWeaponMode();
        swing.setName("swing");
        swing.setDamageDice(2);
        swing.setDamageAdds(1);
        swing.setDamageType(DamageType.CUTTING);
        swing.getReaches().add(1);
        weapon.getModes().add(swing);
        MeleeWeaponMode thrust = new MeleeWeaponMode();
        thrust.setName("thrust");
        thrust.setDamageDice(1);
        thrust.setDamageAdds(1);
        thrust.setDamageType(DamageType.CRUSHING);
        thrust.getReaches().add(1);
        weapon.getModes().add(thrust);
        weapon.setParryType(ParryType.YES);
        weapon.setParryModifier(0);
        weapon.setMinStrength(10);
        weapon.setTwoHanded(false);
        testGameChar.getMeleeWeapons().add(weapon);
        Shield shield = new Shield();
        shield.setName("Medium Shield");
        shield.setSkill(13);
        shield.setDefenseBonus(2);
        testGameChar.getShields().add(shield);
        Armor torso = new Armor();
        torso.setLocation(Location.TORSO);
        torso.setDamageResistance(1);
        testGameChar.getArmorList().add(torso);
        Armor groin = new Armor();
        groin.setLocation(Location.GROIN);
        groin.setDamageResistance(1);
        testGameChar.getArmorList().add(groin);
        Armor legs = new Armor();
        legs.setLocation(Location.LEGS);
        legs.setDamageResistance(1);
        testGameChar.getArmorList().add(legs);
        Armor arms = new Armor();
        arms.setLocation(Location.ARMS);
        arms.setDamageResistance(1);
        testGameChar.getArmorList().add(arms);
        Armor skull = new Armor();
        skull.setLocation(Location.SKULL);
        skull.setDamageResistance(1);
        testGameChar.getArmorList().add(skull);
        Armor face = new Armor();
        face.setLocation(Location.FACE);
        face.setDamageResistance(1);
        testGameChar.getArmorList().add(face);
        Armor hands = new Armor();
        hands.setLocation(Location.HANDS);
        hands.setDamageResistance(1);
        testGameChar.getArmorList().add(hands);
        Armor feet = new Armor();
        feet.setLocation(Location.FEET);
        feet.setDamageResistance(1);
        testGameChar.getArmorList().add(feet);
    }

    @Test
    void createBattle() throws Exception {
        service.createBattle();

        assertTrue(tempFile.exists());
        ObjectMapper mapper = new ObjectMapper();
        Battle battle = mapper.readValue(tempFile, Battle.class);
        assertEquals(0, battle.getCombatants().size());
        assertNull(battle.getNextStep());
        assertEquals(1, battle.getLogs().size());
    }

    @Test
    void deleteBattle() throws Exception {
        Battle battle = new Battle();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(tempFile, battle);

        service.deleteBattle();

        assertFalse(tempFile.exists());
    }

    @Test
    void addCharToBattle_empty_battle() throws Exception {
        // Mock Game Character service.
        List<GameChar> gameChars = new ArrayList<>();
        gameChars.add(testGameChar);
        when(gameCharService.getStoredGameChars()).thenReturn(gameChars);

        // Create empty battle in storage.
        ObjectMapper mapper = new ObjectMapper();
        Battle battle = new Battle();
        battle.getLogs().add(new BattleLog("Battle created."));
        mapper.writeValue(tempFile, battle);

        String label = service.addCharToBattle("Bob the Example");

        assertEquals("Bob the Example", label);
        Battle battle1 = mapper.readValue(tempFile, Battle.class);
        assertEquals(1, battle1.getCombatants().size());
        assertEquals("Bob the Example", battle1.getCombatants().get(0).getLabel());
        assertEquals("Bob the Example", battle1.getCombatants().get(0).getGameChar().getName());
        assertNull(battle1.getNextStep());
        assertEquals(2, battle1.getLogs().size());
    }

    @Test
    void addCharToBattle_char_already_in_battle() throws Exception {
        // Mock Game Character service.
        List<GameChar> gameChars = new ArrayList<>();
        gameChars.add(testGameChar);
        when(gameCharService.getStoredGameChars()).thenReturn(gameChars);

        // Create battle in storage with this char.
        Battle battle = new Battle();
        Set<String> existingLabels = new HashSet<>();
        Combatant combatant = new Combatant(testGameChar, existingLabels);
        battle.getCombatants().add(combatant);
        battle.getLogs().add(new BattleLog("Battle created."));
        battle.getLogs().add(new BattleLog("Combatant Test Character added to Battle."));
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(tempFile, battle);

        String label = service.addCharToBattle("Bob the Example");

        assertEquals("Bob the Example 2", label);
        Battle battle1 = mapper.readValue(tempFile, Battle.class);
        assertEquals(2, battle1.getCombatants().size());
        assertEquals("Bob the Example 2", battle1.getCombatants().get(1).getLabel());
        assertEquals("Bob the Example", battle1.getCombatants().get(1).getGameChar().getName());
        assertNull(battle1.getNextStep());
        assertEquals(3, battle1.getLogs().size());
    }

    @Test
    void removeCharFromBattle() throws Exception {
        // Create battle in storage with a combatant.
        Battle battle = new Battle();
        Set<String> existingLabels = new HashSet<>();
        Combatant combatant = new Combatant(testGameChar, existingLabels);
        battle.getCombatants().add(combatant);
        battle.getLogs().add(new BattleLog("Battle created."));
        battle.getLogs().add(new BattleLog("Combatant Test Character added to Battle."));
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(tempFile, battle);

        service.removeCharFromBattle("Bob the Example");

        // Verify combatant no longer exists in battle.
        Battle battle1 = mapper.readValue(tempFile, Battle.class);
        assertEquals(0, battle1.getCombatants().size());
        assertNull(battle1.getNextStep());
        assertEquals(3, battle1.getLogs().size());
    }

    @Test
    void getBattle() throws Exception {
        // Create battle in storage with a combatant.
        Battle battle = new Battle();
        Set<String> existingLabels = new HashSet<>();
        Combatant combatant = new Combatant(testGameChar, existingLabels);
        battle.getCombatants().add(combatant);
        battle.getLogs().add(new BattleLog("Test Log"));
        battle.getLogs().add(new BattleLog("Another Test Log"));
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(tempFile, battle);

        Battle battle1 = service.getBattle();

        assertEquals(1, battle1.getCombatants().size());
        assertNull(battle1.getNextStep());
        assertEquals(2, battle1.getLogs().size());
    }

    @Test
    void updateBattle() throws Exception {
        // Create battle in storage with a combatant.
        Battle battle = new Battle();
        Set<String> existingLabels = new HashSet<>();
        Combatant combatant = new Combatant(testGameChar, existingLabels);
        battle.getCombatants().add(combatant);
        NextStep nextStep = new NextStep();
        nextStep.setRound(1);
        nextStep.setIndex(2);
        nextStep.setPhase(Phase.END);
        nextStep.setInputNeeded(true);
        nextStep.setMessage("Test Message");
        battle.setNextStep(nextStep);
        battle.getLogs().add(new BattleLog("Test Log 1"));
        battle.getLogs().add(new BattleLog("Test Log 2"));
        battle.getLogs().add(new BattleLog("Test Log 3"));
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(tempFile, battle);

        // Get current battle.
        Battle battle1 = service.getBattle();
        battle1.getCombatants().get(0).setCurrentDamage(4);
        battle1.getNextStep().setRound(2);
        battle1.getNextStep().setIndex(3);
        battle1.getNextStep().setPhase(Phase.BEGIN);
        battle1.getNextStep().setInputNeeded(false);
        battle1.getNextStep().setMessage("Different Test Message");

        service.updateBattle(battle1, "Another Test Log");

        // Verify battle in storage is updated.
        Battle battle2 = mapper.readValue(tempFile, Battle.class);
        assertEquals(4, (int) battle2.getCombatants().get(0).getCurrentDamage());
        assertEquals(2, (int) battle2.getNextStep().getRound());
        assertEquals(3, (int) battle2.getNextStep().getIndex());
        assertEquals(Phase.BEGIN, battle2.getNextStep().getPhase());
        assertFalse(battle2.getNextStep().getInputNeeded());
        assertEquals("Different Test Message", battle2.getNextStep().getMessage());
        assertEquals(4, battle2.getLogs().size());
    }
}