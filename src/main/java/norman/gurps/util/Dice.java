package norman.gurps.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class Dice {
    private static final Logger LOGGER = LoggerFactory.getLogger(Dice.class);
    private Random random = new Random();

    public int roll(int nbrOfDice) {
        return roll(nbrOfDice, 0);
    }

    public int roll(int nbrOfDice, int adds) {
        int total = 0;
        for (int i = 0; i < nbrOfDice; i++) {
            total += random.nextInt(6) + 1;
        }
        return total + adds;
    }
}
