package norman.gurps.combat.model;

public enum ActionType {
    // The description should describe the action such that the following sentence makes sense:
    // "The attacker has chosen to " + actionType.getDescription() + "."
    //@formatter:off
    DO_NOTHING("do nothing"),
    //MOVE("move"),
    //CHANGE_POSTURE("change posture"),
    AIM("aim a ranged weapon"),
    //EVALUATE("evaluate for a melee attack"),
    ATTACK_MELEE("do a regular attack with a melee weapon"),
    ATTACK_RANGED("do a regular attack with a ranged weapon"),
    //FEINT("feint a melee attack"),
    AOA_MELEE_4_TO_HIT("make a determined all-out attack with a melee weapon (gaining +4 to hit)"),
    //AOA_MELEE_TWICE("make a double all-out attack with a melee weapon (attacking twice)"),
    //AOA_MELEE_FEINT("make a feint and then an all-out attack with a melee weapon"),
    AOA_MELEE_2_TO_DMG("make a strong all-out attack with a melee weapon (gaining +2 to damage)"),
    AOA_RANGED_1_TO_HIT("make a determined all-out attack with a ranged weapon (gaining +1 to hit)"),
    //AOA_RANGED_SUPPRESSION("make a suppression fire all-out attack with a ranged weapon"),
    MOVE_ATTACK_MELEE("move and take a wild swing with a melee weapon"),
    MOVE_ATTACK_RANGED("move and take a wild shot with a ranged weapon"),
    AOD_2_TO_DEFENSE("take an increased defense against attacks (gaining +2 to a single defense for each attack)");
    //AOD_TWICE(" take a double defense against attacks (defending twice for each attack)");
    //CONCENTRATE(" concentrate on a spell or something"),
    //READY(" ready an item"),
    //WAIT(" wait");
    //@formatter:on
    private final String description;

    ActionType(String description) {
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
