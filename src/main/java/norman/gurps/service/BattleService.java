package norman.gurps.service;

import norman.gurps.model.battle.Battle;
import norman.gurps.model.battle.Combatant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import static norman.gurps.model.battle.BattleStage.CHOSE_ACTION;

public class BattleService {
    private static Logger LOGGER = LoggerFactory.getLogger(BattleService.class);
    private static ResourceBundle bundle = ResourceBundle.getBundle("message");
    //private static ClassLoader loader = Thread.currentThread().getContextClassLoader();
    //private static ObjectMapper mapper = new ObjectMapper();

    public static List<String> validate(Battle battle) {
        List<String> errors = new ArrayList<>();
        if (battle.getCombatants().isEmpty()) {
            errors.add(bundle.getString("battle.error.no.combatants"));
        }
        List<Combatant> combatants = battle.getCombatants();
        for (Combatant combatant : combatants) {
            errors.addAll(validate(combatant));
        }
        return errors;
    }

    public static List<String> validate(Combatant gameChar) {
        List<String> errors = new ArrayList<>();
        if (gameChar.getStrength() < 0) {
            errors.add(bundle.getString("battle.error.strength.negative"));
        }
        if (gameChar.getDexterity() < 0) {
            errors.add(bundle.getString("battle.error.dexterity.negative"));
        }
        if (gameChar.getIntelligence() < 0) {
            errors.add(bundle.getString("battle.error.intelligence.negative"));
        }
        if (gameChar.getHealth() < 0) {
            errors.add(bundle.getString("battle.error.health.negative"));
        }
        if (gameChar.getBasicSpeed() < 0.0) {
            errors.add(bundle.getString("battle.error.basic.speed.negative"));
        }
        if (gameChar.getDamageResistance() < 0) {
            errors.add(bundle.getString("battle.error.damage.resistance.negative"));
        }
        if (gameChar.getEncumbrance() < 0) {
            errors.add(bundle.getString("battle.error.encumbrance.negative"));
        } else if (gameChar.getEncumbrance() > 4) {
            errors.add(bundle.getString("battle.error.encumbrance.too.high"));
        }
        if (gameChar.getHitPoints() < 0) {
            errors.add(bundle.getString("battle.error.hit.point.negative"));
        }
        if (gameChar.getCurrentHitPoints() < 0) {
            errors.add(bundle.getString("battle.error.current.hit.point.negative"));
        } else if (gameChar.getCurrentHitPoints() > gameChar.getHitPoints()) {
            errors.add(bundle.getString("battle.error.current.hit.point.too.high"));
        }

        return errors;
    }

    public static void start(Battle battle) {
        List<Combatant> combatants = battle.getCombatants();
        combatants.sort(
                Comparator.comparing(Combatant::getBasicSpeed).thenComparing(Combatant::getDexterity).reversed());
        combatants.get(0).setCurrentCombatant(true);
        battle.setStage(CHOSE_ACTION);
        battle.setStarted(true);
    }
}
