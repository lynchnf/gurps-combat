package norman.gurps.combat.service.combat;

import norman.gurps.combat.model.ArmorPiece;
import norman.gurps.combat.model.CombatMelee;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.DamageType;
import norman.gurps.combat.model.HealthStatus;
import norman.gurps.combat.model.HitLocation;
import norman.gurps.combat.model.MeleeWeapon;
import norman.gurps.combat.model.MeleeWeaponMode;
import norman.gurps.combat.model.RangedWeapon;
import norman.gurps.combat.model.ResultType;
import norman.gurps.combat.model.Shield;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CombatUtils {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatUtils.class);

    public int getArmorDamageResistance(HitLocation hitLocation, List<ArmorPiece> armorPieces) {
        int dr = 0;
        for (ArmorPiece armorPiece : armorPieces) {
            if (armorPiece.getHitLocations().contains(hitLocation)) {
                dr += armorPiece.getDamageResistance();
            }
        }
        return dr;
    }

    public Combatant getCombatant(String combatantLabel, List<Combatant> combatants) {
        Combatant found = null;
        for (Combatant combatant : combatants) {
            if (combatantLabel.equals(combatant.getLabel())) {
                found = combatant;
                break;
            }
        }
        return found;
    }

    public CombatMelee getCombatMelee(String weaponName, List<CombatMelee> combatMelees) {
        CombatMelee found = null;
        for (CombatMelee combatMelee : combatMelees) {
            if (weaponName.equals(combatMelee.getWeaponName())) {
                found = combatMelee;
                break;
            }
        }
        return found;
    }

    public int getCurrentMove(HealthStatus healthStatus, int basicMove, int encumbranceLevel) {
        int currentMove = 0;
        if (healthStatus == HealthStatus.ALIVE) {
            currentMove = basicMove - encumbranceLevel;
        } else if (healthStatus == HealthStatus.REELING || healthStatus == HealthStatus.BARELY ||
                healthStatus == HealthStatus.ALMOST || healthStatus == HealthStatus.ALMOST2 ||
                healthStatus == HealthStatus.ALMOST3 || healthStatus == HealthStatus.ALMOST4) {
            currentMove = (int) Math.ceil((basicMove - encumbranceLevel) / 2.0);
        }
        return currentMove;
    }

    public String getDamageDescription(int damageDice, int damageAdds, DamageType damageType) {
        StringBuilder sb = null;
        if (damageDice != 0) {
            sb = new StringBuilder(String.valueOf(damageDice));
            sb.append("d");
        }
        if (damageAdds != 0) {
            if (sb == null) {
                sb = new StringBuilder(String.valueOf(damageAdds));
            } else {
                if (damageAdds > 0) {
                    sb.append("+");
                }
                sb.append(damageAdds);
            }
        }
        if (damageType != null) {
            if (sb == null) {
                sb = new StringBuilder(damageType.toString());
            } else {
                sb.append(" ");
                sb.append(damageType);
            }
        }
        return sb.toString();
    }

    public double getDamageMultiplier(DamageType damageType) {
        double multiplier = 1.0;
        if (damageType == DamageType.SMALL_PIERCING) {
            multiplier = 0.5;
        } else if (damageType == DamageType.CUTTING || damageType == DamageType.LARGE_PIERCING) {
            multiplier = 1.5;
        } else if (damageType == DamageType.IMPALING || damageType == DamageType.HUGE_PIERCING) {
            multiplier = 2.0;
        }
        return multiplier;
    }

    public int getDefenseBonus(List<String> readyItems, List<Shield> shields) {
        // You only get one DB for multiple shields. Use the best one. Kromm has spoken.
        // https://forums.sjgames.com/showpost.php?p=1635191&postcount=8
        int defenseBonus = 0;
        for (String readyItem : readyItems) {
            Shield shield = getShield(readyItem, shields);
            if (shield != null && shield.getDefenseBonus() > defenseBonus) {
                defenseBonus = shield.getDefenseBonus();
            }
        }
        return defenseBonus;
    }

    public HealthStatus getHealthStatus(int hitLevel, boolean unconsciousnessCheckFailed, boolean deathCheckFailed) {
        HealthStatus healthStatus = null;
        if (hitLevel == 1) {
            healthStatus = HealthStatus.ALIVE;
        } else if (hitLevel == 2) {
            healthStatus = HealthStatus.REELING;
        } else if (hitLevel == 3) {
            if (unconsciousnessCheckFailed) {
                healthStatus = HealthStatus.UNCONSCIOUS;
            } else {
                healthStatus = HealthStatus.BARELY;
            }
        } else if (hitLevel == 4) {
            if (deathCheckFailed) {
                healthStatus = HealthStatus.DEAD;
            } else if (unconsciousnessCheckFailed) {
                healthStatus = HealthStatus.UNCONSCIOUS;
            } else {
                healthStatus = HealthStatus.ALMOST;
            }
        } else if (hitLevel == 5) {
            if (deathCheckFailed) {
                healthStatus = HealthStatus.DEAD;
            } else if (unconsciousnessCheckFailed) {
                healthStatus = HealthStatus.UNCONSCIOUS;
            } else {
                healthStatus = HealthStatus.ALMOST2;
            }
        } else if (hitLevel == 6) {
            if (deathCheckFailed) {
                healthStatus = HealthStatus.DEAD;
            } else if (unconsciousnessCheckFailed) {
                healthStatus = HealthStatus.UNCONSCIOUS;
            } else {
                healthStatus = HealthStatus.ALMOST3;
            }
        } else if (hitLevel == 7) {
            if (deathCheckFailed) {
                healthStatus = HealthStatus.DEAD;
            } else if (unconsciousnessCheckFailed) {
                healthStatus = HealthStatus.UNCONSCIOUS;
            } else {
                healthStatus = HealthStatus.ALMOST4;
            }
        } else if (hitLevel == 8) {
            healthStatus = HealthStatus.DEAD;
        } else if (hitLevel == 9) {
            healthStatus = HealthStatus.DESTROYED;
        }
        return healthStatus;
    }

    public int getHitLevel(int hitPoints, int remainingHitPoints) {
        int hitLevel = 0;
        if (remainingHitPoints >= hitPoints / 3.0) {
            hitLevel = 1; // Equivalent to ALIVE.
        } else if (remainingHitPoints > 0) {
            hitLevel = 2; // Equivalent REELING.
        } else if (remainingHitPoints > -1 * hitPoints) {
            hitLevel = 3; // Equivalent to BARELY or UNCONSCIOUS.
        } else if (remainingHitPoints > -2 * hitPoints) {
            hitLevel = 4; // Equivalent to ALMOST or DEAD.
        } else if (remainingHitPoints > -3 * hitPoints) {
            hitLevel = 5; // Equivalent to ALMOST2 or DEAD.
        } else if (remainingHitPoints > -4 * hitPoints) {
            hitLevel = 6; // Equivalent to ALMOST3 or DEAD.
        } else if (remainingHitPoints > -5 * hitPoints) {
            hitLevel = 7; // Equivalent to ALMOST4 or DEAD.
        } else if (remainingHitPoints > -10 * hitPoints) {
            hitLevel = 8; // Equivalent to DEAD.
        } else {
            hitLevel = 9; // Equivalent to DESTROYED;
        }
        return hitLevel;
    }

    public MeleeWeapon getMeleeWeapon(String weaponName, List<MeleeWeapon> meleeWeapons) {
        MeleeWeapon found = null;
        for (MeleeWeapon meleeWeapon : meleeWeapons) {
            if (weaponName.equals(meleeWeapon.getName())) {
                found = meleeWeapon;
            }
        }
        return found;
    }

    public MeleeWeaponMode getMeleeWeaponMode(String weaponModeName, List<MeleeWeaponMode> weaponModes) {
        MeleeWeaponMode found = null;
        for (MeleeWeaponMode weaponMode : weaponModes) {
            if (weaponModeName.equals(weaponMode.getName())) {
                found = weaponMode;
            }
        }
        return found;
    }

    public RangedWeapon getRangedWeapon(String weaponName, List<RangedWeapon> rangedWeapons) {
        RangedWeapon found = null;
        for (RangedWeapon rangedWeapon : rangedWeapons) {
            if (weaponName.equals(rangedWeapon.getName())) {
                found = rangedWeapon;
            }
        }
        return found;
    }

    public ResultType getResultType(int skill, int roll) {
        int margin = skill - roll;
        ResultType result;
        if (roll == 3) {
            result = ResultType.CRITICAL_SUCCESS;
        } else if (roll == 4) {
            result = ResultType.CRITICAL_SUCCESS;
        } else if (roll == 5 && skill >= 15) {
            result = ResultType.CRITICAL_SUCCESS;
        } else if (roll == 6 && skill >= 16) {
            result = ResultType.CRITICAL_SUCCESS;
        } else if (roll == 18) {
            result = ResultType.CRITICAL_FAILURE;
        } else if (roll == 17 && skill <= 15) {
            result = ResultType.CRITICAL_FAILURE;
        } else if (margin <= -10) {
            result = ResultType.CRITICAL_FAILURE;
        } else if (margin >= 0) {
            result = ResultType.SUCCESS;
        } else {
            result = ResultType.FAILURE;
        }
        return result;
    }

    public Shield getShield(String shieldName, List<Shield> shields) {
        Shield found = null;
        for (Shield shield : shields) {
            if (shieldName.equals(shield.getName())) {
                found = shield;
                break;
            }
        }
        return found;
    }

    public int getShockPenalty(int currentDamage) {
        if (currentDamage >= 4) {
            return -4;
        } else {
            return -currentDamage;
        }
    }

    public int getSpeedAndRangePenalty(int speedAndRange) {
        if (speedAndRange <= 2) {
            return 0;
        }
        double scale = Math.floor(Math.log10(speedAndRange));
        double value = speedAndRange / Math.pow(10.0, scale);
        double penalty = -6 * scale;
        if (value <= 1.0) {
            penalty += 2.0;
        } else if (value <= 1.5) {
            penalty += 1.0;
        } else if (value <= 2.0) {
            penalty += 0.0;
        } else if (value <= 3.0) {
            penalty += -1.0;
        } else if (value <= 5.0) {
            penalty += -2.0;
        } else if (value <= 7.0) {
            penalty += -3.0;
        } else {
            penalty += -4.0;
        }
        return (int) penalty;
    }
}
