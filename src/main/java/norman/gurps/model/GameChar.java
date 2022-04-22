package norman.gurps.model;

public class GameChar {
    private int id = -1;
    private String name;
    private int strength = 10;
    private int dexterity = 10;
    private int intelligence = 10;
    private int health = 10;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getDexterity() {
        return dexterity;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    @Override
    public String toString() {
        return "GameChar{" + "id=" + id + ", name='" + name + '\'' + ", strength=" + strength + ", dexterity=" +
                dexterity + ", intelligence=" + intelligence + ", health=" + health + '}';
    }
}
