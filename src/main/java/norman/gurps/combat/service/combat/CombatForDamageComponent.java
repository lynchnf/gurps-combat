package norman.gurps.combat.service.combat;

import norman.gurps.combat.exception.LoggingException;
import norman.gurps.combat.model.ActionType;
import norman.gurps.combat.model.CombatAttack;
import norman.gurps.combat.model.CombatMelee;
import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.CombatRanged;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.DamageType;
import norman.gurps.combat.model.HealthStatus;
import norman.gurps.combat.model.HitLocation;
import norman.gurps.combat.model.MeleeWeapon;
import norman.gurps.combat.model.MeleeWeaponMode;
import norman.gurps.combat.model.NextStep;
import norman.gurps.combat.model.RangedWeapon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CombatForDamageComponent {
    private static Logger LOGGER = LoggerFactory.getLogger(CombatForDamageComponent.class);
    private CombatUtils utils;

    public CombatForDamageComponent(CombatUtils utils) {
        this.utils = utils;
    }

    public NextStep promptAndUpdateAttacker(int round, int index, Combatant attacker) {
        String damageDescription;
        if (!attacker.getCombatMelees().isEmpty()) {
            int size = attacker.getCombatMelees().size();
            CombatMelee combatMelee = attacker.getCombatMelees().get(size - 1);
            MeleeWeapon weapon = utils.getMeleeWeapon(combatMelee.getWeaponName(),
                    attacker.getGameChar().getMeleeWeapons());
            MeleeWeaponMode weaponMode = utils.getMeleeWeaponMode(combatMelee.getWeaponModeName(),
                    weapon.getMeleeWeaponModes());
            int damageDice = weaponMode.getDamageDice();
            int damageAdds = weaponMode.getDamageAdds();
            if (attacker.getActionType() == ActionType.AOA_MELEE_2_TO_DMG) {
                damageAdds += 2;
            }
            DamageType damageType = weaponMode.getDamageType();
            combatMelee.setDamageDice(damageDice);
            combatMelee.setDamageAdds(damageAdds);
            combatMelee.setDamageType(damageType);
            damageDescription = utils.getDamageDescription(damageDice, damageAdds, damageType);
        } else {
            CombatRanged combatRanged = attacker.getCombatRanged();
            String weaponName = combatRanged.getWeaponName();
            RangedWeapon weapon = utils.getRangedWeapon(weaponName, attacker.getGameChar().getRangedWeapons());
            int damageDice = weapon.getDamageDice();
            int damageAdds = weapon.getDamageAdds();
            DamageType damageType = weapon.getDamageType();
            combatRanged.setDamageDice(damageDice);
            combatRanged.setDamageAdds(damageAdds);
            combatRanged.setDamageType(damageType);
            damageDescription = utils.getDamageDescription(damageDice, damageAdds, damageType);
        }
        String message = attacker.getLabel() + ", please roll " + damageDescription + " damage.";

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(CombatPhase.RESOLVE_DAMAGE);
        nextStep.setInputNeeded(true);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }

    public void validate(Integer forDamageRoll, Combatant attacker) {
        // Validate for damage roll.
        if (forDamageRoll == null) {
            throw new LoggingException(LOGGER, "Value rolled for damage may not be blank.");
        } else {
            CombatAttack combatAttack;
            if (!attacker.getCombatMelees().isEmpty()) {
                int size = attacker.getCombatMelees().size();
                combatAttack = attacker.getCombatMelees().get(size - 1);
            } else {
                combatAttack = attacker.getCombatRanged();
            }

            int damageDice = combatAttack.getDamageDice();
            int damageAdds = combatAttack.getDamageAdds();
            int minDamage = damageDice + damageAdds;
            int maxDamage = 6 * damageDice + damageAdds;

            if (forDamageRoll < minDamage || forDamageRoll > maxDamage) {
                throw new LoggingException(LOGGER,
                        "For damage roll must be between " + minDamage + " and " + maxDamage + ".");
            }
        }
    }

    public void updateAttacker(Combatant attacker, int forDamageRoll, Combatant target) {
        int naturalDamageResistance = target.getGameChar().getDamageResistance();
        int armorDamageResistance = utils.getArmorDamageResistance(HitLocation.TORSO,
                target.getGameChar().getArmorPieces());
        int targetDamageResistance = naturalDamageResistance + armorDamageResistance;
        int penetratingDamage = forDamageRoll - targetDamageResistance;

        CombatAttack combatAttack;
        if (!attacker.getCombatMelees().isEmpty()) {
            int size = attacker.getCombatMelees().size();
            combatAttack = attacker.getCombatMelees().get(size - 1);
        } else {
            combatAttack = attacker.getCombatRanged();
        }

        DamageType damageType = combatAttack.getDamageType();
        double damageMultiplier = utils.getDamageMultiplier(damageType);
        int injuryDamage = (int) (penetratingDamage * damageMultiplier);
        if (penetratingDamage > 0 && injuryDamage == 0) {
            injuryDamage = 1;
        }

        // Update combatant melee.
        combatAttack.setForDamageRoll(forDamageRoll);
        combatAttack.setTargetDamageResistance(targetDamageResistance);
        combatAttack.setPenetratingDamage(penetratingDamage);
        combatAttack.setInjuryDamage(injuryDamage);
    }

    public void updateTarget(Combatant target, Combatant attacker) {
        CombatAttack combatAttack;
        if (!attacker.getCombatMelees().isEmpty()) {
            int size = attacker.getCombatMelees().size();
            combatAttack = attacker.getCombatMelees().get(size - 1);
        } else {
            combatAttack = attacker.getCombatRanged();
        }
        int injuryDamage = combatAttack.getInjuryDamage();

        int hitPoints = target.getGameChar().getHitPoints();
        int previousDamage = target.getPreviousDamage();
        int oldCurrentDamage = target.getCurrentDamage();
        int newCurrentDamage = oldCurrentDamage + injuryDamage;
        int shockPenalty = utils.getShockPenalty(newCurrentDamage);
        int oldRemainingHitPoints = hitPoints - oldCurrentDamage - previousDamage;
        int newRemainingHitPoints = hitPoints - newCurrentDamage - previousDamage;
        int oldHitLevel = utils.getHitLevel(hitPoints, oldRemainingHitPoints);
        int newHitLevel = utils.getHitLevel(hitPoints, newRemainingHitPoints);
        Boolean unconsciousnessCheckFailed = target.getUnconsciousnessCheckFailed();
        Boolean deathCheckFailed = target.getDeathCheckFailed();
        HealthStatus healthStatus = utils.getHealthStatus(newHitLevel, unconsciousnessCheckFailed, deathCheckFailed);
        int basicMove = target.getGameChar().getBasicMove();
        int encumbranceLevel = target.getGameChar().getEncumbranceLevel();
        int newCurrentMove = utils.getCurrentMove(healthStatus, basicMove, encumbranceLevel);

        // If the target's new status is between ALMOST and ALMOST4 and the target's old status is less and the target
        // has not yet failed a death check, then the target will need to make one or more rolls to avoid death.
        int nbrOfDeathChecksNeeded = 0;
        if (newHitLevel >= 4 && newHitLevel <= 7 && newHitLevel > oldHitLevel) {
            if (oldHitLevel <= 3) {
                oldHitLevel = 3;
            }
            nbrOfDeathChecksNeeded = newHitLevel - oldHitLevel;
        }

        // Update target.
        target.setCurrentDamage(newCurrentDamage);
        target.setShockPenalty(shockPenalty);
        target.setNbrOfDeathChecksNeeded(nbrOfDeathChecksNeeded);
        target.setHealthStatus(healthStatus);
        target.setCurrentMove(newCurrentMove);
    }

    public NextStep resolve(int round, int index, Combatant attacker, Combatant target) {
        CombatAttack combatAttack;
        if (!attacker.getCombatMelees().isEmpty()) {
            int size = attacker.getCombatMelees().size();
            combatAttack = attacker.getCombatMelees().get(size - 1);
        } else {
            combatAttack = attacker.getCombatRanged();
        }
        int forDamageRoll = combatAttack.getForDamageRoll();
        DamageType damageType = combatAttack.getDamageType();
        int penetratingDamage = combatAttack.getPenetratingDamage();
        int targetDamageResistance = combatAttack.getTargetDamageResistance();
        int injuryDamage = combatAttack.getInjuryDamage();

        int nbrOfDeathChecksNeeded = target.getNbrOfDeathChecksNeeded();

        CombatPhase combatPhase = CombatPhase.END_TURN;
        if (nbrOfDeathChecksNeeded > 0) {
            combatPhase = CombatPhase.PROMPT_FOR_DEATH_CHECK;
        }
        String message = target.getLabel() + " is hit for " + forDamageRoll + " " + damageType + " damage. " +
                penetratingDamage + " gets through his/her armour (DR " + targetDamageResistance + ") doing " +
                injuryDamage + " hits of damage.";

        // Create next step.
        NextStep nextStep = new NextStep();
        nextStep.setRound(round);
        nextStep.setIndex(index);
        nextStep.setCombatPhase(combatPhase);
        nextStep.setInputNeeded(false);
        nextStep.setMessage("" + round + "/" + index + " : " + message);
        return nextStep;
    }
}
