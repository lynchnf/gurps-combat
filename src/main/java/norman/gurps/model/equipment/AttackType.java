package norman.gurps.model.equipment;

public enum AttackType {
    SWING("sw"), THRUST("thr");

    private String abbreviation;

    AttackType(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}
