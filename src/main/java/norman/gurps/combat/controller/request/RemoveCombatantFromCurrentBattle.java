package norman.gurps.combat.controller.request;

@Deprecated
public class RemoveCombatantFromCurrentBattle {
    private String label;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RemoveCombatantFromCurrentBattle{");
        sb.append("label='").append(label).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
