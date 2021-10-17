package norman.gurps.equipment;

public enum DamageType {
    // @formatter:off
    BURNING(1.0),
    CRUSHING(1.0),
    CUTTING(1.5),
    IMPALING(2.0),
    SMALL_PIERCING(0.5),
    PIERCING(1.0),
    LARGE_PIERCING(1.5);
    // @formatter:on

    private double woundingModifier;

    DamageType(double woundingModifier) {
        this.woundingModifier = woundingModifier;
    }

    public double getWoundingModifier() {
        return woundingModifier;
    }
}
