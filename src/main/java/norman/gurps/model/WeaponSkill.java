package norman.gurps.model;

import java.util.ArrayList;
import java.util.List;

public class WeaponSkill {
    private String skillName;
    private Integer minimumStrength;
    private Boolean unReadied;
    private final List<WeaponMode> modes = new ArrayList<>();
}
