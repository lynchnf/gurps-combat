package norman.gurps.gui.gamechar;

import norman.gurps.gui.ButtonDescriptor;
import norman.gurps.model.gamechar.CharWeapon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharWeaponTableRow {
    private static final Logger LOGGER = LoggerFactory.getLogger(CharWeaponTableRow.class);
    private final CharWeapon charWeapon;
    private ButtonDescriptor buttonDescriptor;

    public CharWeaponTableRow(CharWeapon charWeapon, ButtonDescriptor buttonDescriptor) {
        this.charWeapon = charWeapon;
        this.buttonDescriptor = buttonDescriptor;
    }

    public ButtonDescriptor getButtonDescriptor() {
        return buttonDescriptor;
    }

    public void setButtonDescriptor(ButtonDescriptor buttonDescriptor) {
        this.buttonDescriptor = buttonDescriptor;
    }

    public String getWeaponName() {
        return charWeapon.getWeaponName();
    }

    public void setWeaponName(String weaponName) {
        charWeapon.setWeaponName(weaponName);
    }

    public String getSkillName() {
        return charWeapon.getSkillName();
    }

    public void setSkillName(String skillName) {
        charWeapon.setSkillName(skillName);
    }

    public Integer getSkillLevel() {
        return charWeapon.getSkillLevel();
    }

    public void setSkillLevel(Integer skillLevel) {
        charWeapon.setSkillLevel(skillLevel);
    }
}
