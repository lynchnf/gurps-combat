package norman.gurps.combat.model;

import norman.gurps.combat.TestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CombatantTest {
    GameChar testGameChar;

    @BeforeEach
    void setUp() {
        testGameChar = TestHelper.getGameChar1();
    }

    @Test
    void constructor1() {
        Combatant combatant = new Combatant(testGameChar, new HashSet<>());

        assertEquals("Bob the Example", combatant.getLabel());
    }

    @Test
    void constructor2() {
        Set<String> existingLabels = new HashSet<>();
        existingLabels.add("Bob the Example");

        Combatant combatant = new Combatant(testGameChar, existingLabels);

        assertEquals("Bob the Example 2", combatant.getLabel());
    }

    @Test
    void constructor3() {
        Set<String> existingLabels = new HashSet<>();
        existingLabels.add("Bob the Example");
        existingLabels.add("Bob the Example 2");

        Combatant combatant = new Combatant(testGameChar, existingLabels);

        assertEquals("Bob the Example 3", combatant.getLabel());
    }
}