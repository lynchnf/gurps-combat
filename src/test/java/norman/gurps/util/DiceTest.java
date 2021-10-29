package norman.gurps.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class DiceTest {
    private Dice dice;

    @BeforeEach
    void setUp() {
        dice = new Dice();
    }

    @AfterEach
    void tearDown() {
        dice = null;
    }

    @Test
    void roll() {
        int nbrOfTests = 10000;
        Map<Integer, Integer> counts = new HashMap<>();
        for (int i = 0; i < nbrOfTests; i++) {
            int roll = dice.roll(3);
            Integer key = Integer.valueOf(roll);
            Integer value = counts.get(key);
            if (value == null) {
                value = Integer.valueOf(1);
            } else {
                int oldValue = value.intValue();
                value = Integer.valueOf(oldValue + 1);
            }
            counts.put(key, value);
        }

        for (int i = 3; i <= 18; i++) {
            Integer key = Integer.valueOf(i);
            double percent = counts.get(key) == null ? 0 : counts.get(key).intValue();
            percent = percent * 100.0 / nbrOfTests;
            System.out.printf("%2d : %5.2f%%%n", i, percent);
        }
    }
}