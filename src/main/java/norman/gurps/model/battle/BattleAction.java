package norman.gurps.model.battle;

public enum BattleAction {
    DO_NOTHING("combatant.action.do.nothing"), ATTACK("combatant.action.attack"),
    ALL_OUT_ATTACK_DETERMINED("combatant.action.all.out.attack.determined"),
    ALL_OUT_ATTACK_DOUBLE("combatant.action.all.out.attack.double"),
    ALL_OUT_ATTACK_STRONG("combatant.action.all.out.attack.strong"),
    ALL_OUT_DEFENCE_INCREASED("combatant.action.all.out.defense.increased"),
    ALL_OUT_DEFENCE_DOUBLE("combatant.action.all.out.defense.double");

    private String key;

    BattleAction(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
