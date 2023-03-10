package norman.gurps.combat.model;

public class Armor {
    private Location location;
    private Integer damageResistance;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Integer getDamageResistance() {
        return damageResistance;
    }

    public void setDamageResistance(Integer damageResistance) {
        this.damageResistance = damageResistance;
    }
}