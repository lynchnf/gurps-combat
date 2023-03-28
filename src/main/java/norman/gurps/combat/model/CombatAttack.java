package norman.gurps.combat.model;

public interface CombatAttack {
    String getTargetLabel();

    void setTargetLabel(String targetLabel);

    String getWeaponName();

    void setWeaponName(String weaponName);

    Integer getToHitEffectiveSkill();

    void setToHitEffectiveSkill(Integer toHitEffectiveSkill);

    Integer getToHitRoll();

    void setToHitRoll(Integer toHitRoll);

    ResultType getToHitResultType();

    void setToHitResultType(ResultType toHitResultType);

    Integer getDamageDice();

    void setDamageDice(Integer damageDice);

    Integer getDamageAdds();

    void setDamageAdds(Integer damageAdds);

    DamageType getDamageType();

    void setDamageType(DamageType damageType);

    Integer getForDamageRoll();

    void setForDamageRoll(Integer forDamageRoll);

    Integer getTargetDamageResistance();

    void setTargetDamageResistance(Integer targetDamageResistance);

    Integer getPenetratingDamage();

    void setPenetratingDamage(Integer penetratingDamage);

    Integer getInjuryDamage();

    void setInjuryDamage(Integer injuryDamage);
}
