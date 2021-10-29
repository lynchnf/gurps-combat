package norman.gurps.equipment;

/**
 * Bean that contains armor as listed in GURPS Lite, page 18.
 */
public class Armor extends Item {
    private int damageResistance;

    public int getDamageResistance() {
        return damageResistance;
    }

    public void setDamageResistance(int damageResistance) {
        this.damageResistance = damageResistance;
    }
}
