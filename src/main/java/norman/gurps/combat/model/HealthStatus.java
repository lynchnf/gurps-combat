package norman.gurps.combat.model;

public enum HealthStatus {
    ALIVE("Alive and well"), REELING("Reeling from wounds"), BARELY("Barely consciousness"),
    unconsciousness("unconsciousness"), ALMOST("Almost dead"), DEAD("Dead");
    private final String description;

    HealthStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
