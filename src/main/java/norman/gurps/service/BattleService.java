package norman.gurps.service;

import norman.gurps.model.battle.Battle;
import norman.gurps.model.battle.Combatant;
import norman.gurps.model.battle.CombatantWeapon;
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

    public static List<String> validate(Combatant combatant) {
        List<String> errors = new ArrayList<>();
        if (combatant.getStrength() < 0) {
            errors.add(bundle.getString("battle.error.strength.negative"));
        }
        if (combatant.getDexterity() < 0) {
            errors.add(bundle.getString("battle.error.dexterity.negative"));
        }
        if (combatant.getIntelligence() < 0) {
            errors.add(bundle.getString("battle.error.intelligence.negative"));
        }
        if (combatant.getHealth() < 0) {
            errors.add(bundle.getString("battle.error.health.negative"));
        }
        if (combatant.getBasicSpeed() < 0.0) {
            errors.add(bundle.getString("battle.error.basic.speed.negative"));
        }
        if (combatant.getDamageResistance() < 0) {
            errors.add(bundle.getString("battle.error.damage.resistance.negative"));
        }
        if (combatant.getEncumbrance() < 0) {
            errors.add(bundle.getString("battle.error.encumbrance.negative"));
        } else if (combatant.getEncumbrance() > 4) {
            errors.add(bundle.getString("battle.error.encumbrance.too.high"));
        }
        if (combatant.getHitPoints() < 0) {
            errors.add(bundle.getString("battle.error.hit.point.negative"));
        }
        if (combatant.getCurrentHitPoints() < 0) {
            errors.add(bundle.getString("battle.error.current.hit.point.negative"));
        } else if (combatant.getCurrentHitPoints() > combatant.getHitPoints()) {
            errors.add(bundle.getString("battle.error.current.hit.point.too.high"));
        }
        CombatantWeapon weapon = combatant.getCombatantWeapons().get(combatant.getReadyWeaponIndex());
        if (weapon.getTwoHanded() && combatant.getShieldReady()) {
            errors.add(bundle.getString("battle.error.shield.cannot.be.ready"));
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
