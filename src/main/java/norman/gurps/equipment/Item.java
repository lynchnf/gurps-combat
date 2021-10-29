package norman.gurps.equipment;

import java.math.BigDecimal;

/**
 * Base class for armor, shields, and weapons. Could also be used for miscellaneous equipment that is none of the
 * above.
 */
public class Item {
    private String name;
    private BigDecimal cost;
    private double weight;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
