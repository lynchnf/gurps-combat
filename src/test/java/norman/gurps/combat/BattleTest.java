package norman.gurps.combat;

import norman.gurps.character.GameCharacter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BattleTest {
    private Battle battle;

    @Before
    public void setUp() throws Exception {
        GameCharacter able = new GameCharacter();
        GameCharacter baker = new GameCharacter();
        battle = new Battle();
        battle.addCombatant(able, "blue");
        battle.addCombatant(baker, "red");
    }

    @After
    public void tearDown() throws Exception {
        battle = null;
    }

    @Test
    public void addCombatant() {
    }

    @Test
    public void start() {
    }

    @Test
    public void next() {
    }

    @Test
    public void getRound() {
    }

    @Test
    public void getActor() {
    }

    @Test
    public void setTargetIndex() {
    }

    @Test
    public void getTarget() {
    }

    @Test
    public void executeAction() {
    }

    @Test
    public void executeDefense() {
    }

    @Test
    public void applyDamage() {
    }

    @Test
    public void getStayConscious() {
    }

    @Test
    public void over() {
    }
}