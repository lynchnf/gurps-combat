package norman.gurps.combat.service.combat;

import norman.gurps.combat.TestHelper;
import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.NextStep;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class CombatEndTurnComponentTest {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatEndTurnComponentTest.class);
    CombatEndTurnComponent component;
    Combatant attacker;

    @BeforeEach
    void setUp() {
        component = new CombatEndTurnComponent();
        attacker = TestHelper.getCombatant(TestHelper.getGameChar1());
    }

    @Test
    void updateAttacker() {
        component.updateAttacker(attacker);

        Assertions.assertEquals(0, attacker.getShockPenalty());
    }

    @Test
    void resolve() {
        NextStep nextStep = component.resolve(1, 0, 3);

        assertEquals(1, nextStep.getRound());
        assertEquals(1, nextStep.getIndex());
        assertEquals(CombatPhase.BEGIN_TURN, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNull(nextStep.getMessage());
    }
}
