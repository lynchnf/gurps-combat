package norman.gurps.character;

import norman.gurps.equipment.Shield;

public class CharacterShield {
    private GameCharacter character;
    private String label;
    private Shield shield;
    private boolean primary = false;

    public CharacterShield(GameCharacter character, String label, Shield shield) {
        this(character, label, shield, false);
    }

    public CharacterShield(GameCharacter character, String label, Shield shield, boolean primary) {
        this.character = character;
        this.label = label;
        this.shield = shield;
        this.primary = primary;
    }

    public int getBlock() {
        String skillName = shield.getSkillName();
        int level = character.getSkill(skillName).getLevel();
        int defenseBonus = character.getShieldDefenseBonus();
        return level / 2 + 3 + defenseBonus;
    }

    public String getLabel() {
        return label;
    }

    public Shield getShield() {
        return shield;
    }

    public boolean isPrimary() {
        return primary;
    }
}
