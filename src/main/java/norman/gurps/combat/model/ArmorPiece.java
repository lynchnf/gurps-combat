package norman.gurps.combat.model;

import java.util.ArrayList;
import java.util.List;

public class ArmorPiece {
    private String name;
    private List<HitLocation> hitLocations = new ArrayList<>();
    private Integer damageResistance;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<HitLocation> getHitLocations() {
        return hitLocations;
    }

    public void setHitLocations(List<HitLocation> hitLocations) {
        this.hitLocations = hitLocations;
    }

    public Integer getDamageResistance() {
        return damageResistance;
    }

    public void setDamageResistance(Integer damageResistance) {
        this.damageResistance = damageResistance;
    }
}
