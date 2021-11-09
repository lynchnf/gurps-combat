package norman.gurps.combat;

/**
 * List of possible character actions.
 */
public enum Maneuver {
    // @formatter:off
    DO_NOTHING(false, false),
    MOVE(false, false),
    CHANGE_POSTURE(false, false),
    AIM(true, false),
    ATTACK(true, true),
    ALL_OUT_ATTACK_DETERMINED(true, true),
    ALL_OUT_ATTACK_DOUBLE(true, true),
    ALL_OUT_ATTACK_STRONG(true, true),
    ALL_OUT_DEFENSE_INCREASED(false, false),
    ALL_OUT_DEFENSE_DOUBLE(false, false),
    MOVE_AND_ATTACK(true, true),
    CONCENTRATE(false, false),
    READY(false, false);
    // @formatter:on

    private boolean targetRequired;
    private boolean damageDone;

    Maneuver(boolean targetRequired, boolean damageDone) {
        this.targetRequired = targetRequired;
        this.damageDone = damageDone;
    }

    public boolean isTargetRequired() {
        return targetRequired;
    }

    public boolean isDamageDone() {
        return damageDone;
    }
}