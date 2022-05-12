package norman.gurps.model.equipment;

public enum DamageType {
    BURNING("burn"), CRUSHING("cr"), CUTTING("cut"), IMPALING("imp"), SMALL_PIERCING("pi-"), PIERCING("pi"),
    LARGE_PIERCING("pi+");

    private String abbreviation;

    DamageType(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}
