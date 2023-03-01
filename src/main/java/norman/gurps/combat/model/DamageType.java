package norman.gurps.combat.model;

public enum DamageType {
    AFFLICTION("aff"), BURNING("burn"), CORROSION("cor"), CRUSHING("cr"), CUTTING("cut"), FATIGUE("fat"),
    IMPALING("imp"), SMALL_PIERCING("pi-"), PIERCING("pi"), LARGE_PIERCING("pi+"), HUGE_PIERCING("pi++"),
    SPECIAL("spec."), TOXIC("tox");
    private String abbreviation;

    DamageType(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    @Override
    public String toString() {
        return abbreviation;
    }
}
