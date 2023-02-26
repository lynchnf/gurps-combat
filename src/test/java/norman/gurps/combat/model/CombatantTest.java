package norman.gurps.combat.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CombatantTest {
    GameChar testGameChar;

    @BeforeEach
    void setUp() {
        testGameChar = new GameChar();
        testGameChar.setName("Test Character Name");
        testGameChar.setStrength(14);
        testGameChar.setDexterity(13);
        testGameChar.setIntelligence(12);
        testGameChar.setHealth(11);
    }

    @Test
    void constructor1() {
        Combatant combatant = new Combatant(testGameChar, new HashSet<>());

        assertEquals("Test Character Name", combatant.getLabel());
    }

    @Test
    void constructor2() {
        Set<String> existingLabels = new HashSet<>();
        existingLabels.add("Test Character Name");

        Combatant combatant = new Combatant(testGameChar, existingLabels);

        assertEquals("Test Character Name 2", combatant.getLabel());
    }

    @Test
    void constructor3() {
        Set<String> existingLabels = new HashSet<>();
        existingLabels.add("Test Character Name");
        existingLabels.add("Test Character Name 2");

        Combatant combatant = new Combatant(testGameChar, existingLabels);

        assertEquals("Test Character Name 3", combatant.getLabel());
    }
}