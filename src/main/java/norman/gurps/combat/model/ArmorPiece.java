package norman.gurps.combat.model;

public class ArmorPiece {
    private HitLocation hitLocation;
    private Integer damageResistance;

    public HitLocation getHitLocation() {
        return hitLocation;
    }

    public void setHitLocation(HitLocation hitLocation) {
        this.hitLocation = hitLocation;
    }

    public Integer getDamageResistance() {
        return damageResistance;
    }

    public void setDamageResistance(Integer damageResistance) {
        this.damageResistance = damageResistance;
    }
}
