package norman.gurps.combat;

public class DamageResult {
    private String damage;
    private int rollValue;
    private int penetratingDamage;
    private int finalDamage;

    public String getDamage() {
        return damage;
    }

    public void setDamage(String damage) {
        this.damage = damage;
    }

    public int getRollValue() {
        return rollValue;
    }

    public void setRollValue(int rollValue) {
        this.rollValue = rollValue;
    }

    public int getPenetratingDamage() {
        return penetratingDamage;
    }

    public void setPenetratingDamage(int penetratingDamage) {
        this.penetratingDamage = penetratingDamage;
    }

    public int getFinalDamage() {
        return finalDamage;
    }

    public void setFinalDamage(int finalDamage) {
        this.finalDamage = finalDamage;
    }
}
