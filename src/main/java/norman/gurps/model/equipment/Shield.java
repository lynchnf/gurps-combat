package norman.gurps.model.equipment;

public class Shield {
    private String name;
    private Integer defenseBonus;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDefenseBonus() {
        return defenseBonus;
    }

    public void setDefenseBonus(Integer defenseBonus) {
        this.defenseBonus = defenseBonus;
    }

    @Override
    public String toString() {
        return name;
    }
}
