package norman.gurps.combat.model;

public enum HealthStatus {
    //@formatter:off
    ALIVE("alive and well"),
    REELING("reeling from wounds"),
    BARELY("barely conscious"),
    UNCONSCIOUS("unconscious"),
    ALMOST("almost dead"),
    ALMOST2("almost dead 2"),
    ALMOST3("almost dead 3"),
    ALMOST4("almost dead 4"),
    DEAD("dead and gone"),
    DESTROYED("completely destroyed");
    //@formatter:on
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