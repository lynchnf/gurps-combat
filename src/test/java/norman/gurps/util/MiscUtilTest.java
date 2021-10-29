package norman.gurps.util;

import norman.gurps.equipment.Armor;
import norman.gurps.equipment.Shield;
import norman.gurps.equipment.Weapon;
import norman.gurps.skill.Skill;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MiscUtilTest {
    @Test
    void getThrustDamageDice() {
        assertEquals(1, MiscUtil.getThrustDamageDice(6));
        assertEquals(1, MiscUtil.getThrustDamageDice(10));
        assertEquals(1, MiscUtil.getThrustDamageDice(14));
        assertEquals(1, MiscUtil.getThrustDamageDice(18));
        assertEquals(2, MiscUtil.getThrustDamageDice(22));
    }

    @Test
    void getThrustDamageAdds() {
        assertEquals(-4, MiscUtil.getThrustDamageAdds(6));
        assertEquals(-2, MiscUtil.getThrustDamageAdds(10));
        assertEquals(0, MiscUtil.getThrustDamageAdds(14));
        assertEquals(2, MiscUtil.getThrustDamageAdds(18));
        assertEquals(0, MiscUtil.getThrustDamageAdds(22));
    }

    @Test
    void getSwingDamageDice() {
        assertEquals(1, MiscUtil.getSwingDamageDice(6));
        assertEquals(1, MiscUtil.getSwingDamageDice(10));
        assertEquals(2, MiscUtil.getSwingDamageDice(14));
        assertEquals(3, MiscUtil.getSwingDamageDice(18));
        assertEquals(4, MiscUtil.getSwingDamageDice(22));
    }

    @Test
    void getSwingDamageAdds() {
        assertEquals(-3, MiscUtil.getSwingDamageAdds(6));
        assertEquals(0, MiscUtil.getSwingDamageAdds(10));
        assertEquals(0, MiscUtil.getSwingDamageAdds(14));
        assertEquals(0, MiscUtil.getSwingDamageAdds(18));
        assertEquals(0, MiscUtil.getSwingDamageAdds(22));
    }

    @Test
    void calculateStatus() {
        assertEquals(RollStatus.CRITICAL_SUCCESS, MiscUtil.calculateStatus(5, 3));
        assertEquals(RollStatus.CRITICAL_SUCCESS, MiscUtil.calculateStatus(5, 4));
        assertEquals(RollStatus.SUCCESS, MiscUtil.calculateStatus(5, 5));
        assertEquals(RollStatus.FAILURE, MiscUtil.calculateStatus(5, 6));
        assertEquals(RollStatus.FAILURE, MiscUtil.calculateStatus(5, 7));
        assertEquals(RollStatus.CRITICAL_FAILURE, MiscUtil.calculateStatus(5, 18));
        assertEquals(RollStatus.CRITICAL_FAILURE, MiscUtil.calculateStatus(5, 17));
        assertEquals(RollStatus.CRITICAL_FAILURE, MiscUtil.calculateStatus(5, 16));
        assertEquals(RollStatus.CRITICAL_FAILURE, MiscUtil.calculateStatus(5, 15));
        assertEquals(RollStatus.FAILURE, MiscUtil.calculateStatus(5, 14));

        assertEquals(RollStatus.CRITICAL_SUCCESS, MiscUtil.calculateStatus(10, 3));
        assertEquals(RollStatus.CRITICAL_SUCCESS, MiscUtil.calculateStatus(10, 4));
        assertEquals(RollStatus.SUCCESS, MiscUtil.calculateStatus(10, 5));
        assertEquals(RollStatus.SUCCESS, MiscUtil.calculateStatus(10, 6));
        assertEquals(RollStatus.SUCCESS, MiscUtil.calculateStatus(10, 7));
        assertEquals(RollStatus.CRITICAL_FAILURE, MiscUtil.calculateStatus(10, 18));
        assertEquals(RollStatus.CRITICAL_FAILURE, MiscUtil.calculateStatus(10, 17));
        assertEquals(RollStatus.FAILURE, MiscUtil.calculateStatus(10, 16));
        assertEquals(RollStatus.FAILURE, MiscUtil.calculateStatus(10, 15));
        assertEquals(RollStatus.FAILURE, MiscUtil.calculateStatus(10, 14));

        assertEquals(RollStatus.CRITICAL_SUCCESS, MiscUtil.calculateStatus(15, 3));
        assertEquals(RollStatus.CRITICAL_SUCCESS, MiscUtil.calculateStatus(15, 4));
        assertEquals(RollStatus.CRITICAL_SUCCESS, MiscUtil.calculateStatus(15, 5));
        assertEquals(RollStatus.SUCCESS, MiscUtil.calculateStatus(15, 6));
        assertEquals(RollStatus.SUCCESS, MiscUtil.calculateStatus(15, 7));
        assertEquals(RollStatus.CRITICAL_FAILURE, MiscUtil.calculateStatus(15, 18));
        assertEquals(RollStatus.CRITICAL_FAILURE, MiscUtil.calculateStatus(15, 17));
        assertEquals(RollStatus.FAILURE, MiscUtil.calculateStatus(15, 16));
        assertEquals(RollStatus.SUCCESS, MiscUtil.calculateStatus(15, 15));
        assertEquals(RollStatus.SUCCESS, MiscUtil.calculateStatus(15, 14));

        assertEquals(RollStatus.CRITICAL_SUCCESS, MiscUtil.calculateStatus(20, 3));
        assertEquals(RollStatus.CRITICAL_SUCCESS, MiscUtil.calculateStatus(20, 4));
        assertEquals(RollStatus.CRITICAL_SUCCESS, MiscUtil.calculateStatus(20, 5));
        assertEquals(RollStatus.CRITICAL_SUCCESS, MiscUtil.calculateStatus(20, 6));
        assertEquals(RollStatus.SUCCESS, MiscUtil.calculateStatus(20, 7));
        assertEquals(RollStatus.CRITICAL_FAILURE, MiscUtil.calculateStatus(20, 18));
        assertEquals(RollStatus.FAILURE, MiscUtil.calculateStatus(20, 17));
        assertEquals(RollStatus.SUCCESS, MiscUtil.calculateStatus(20, 16));
        assertEquals(RollStatus.SUCCESS, MiscUtil.calculateStatus(20, 15));
        assertEquals(RollStatus.SUCCESS, MiscUtil.calculateStatus(20, 14));
    }

    @Test
    void calculateSimpleStatus() {
        assertEquals(RollStatus.SUCCESS, MiscUtil.calculateSimpleStatus(20, 3));
        assertEquals(RollStatus.SUCCESS, MiscUtil.calculateSimpleStatus(20, 4));
        assertEquals(RollStatus.SUCCESS, MiscUtil.calculateSimpleStatus(20, 5));
        assertEquals(RollStatus.SUCCESS, MiscUtil.calculateSimpleStatus(20, 6));
        assertEquals(RollStatus.SUCCESS, MiscUtil.calculateSimpleStatus(20, 7));

        assertEquals(RollStatus.FAILURE, MiscUtil.calculateSimpleStatus(5, 18));
        assertEquals(RollStatus.FAILURE, MiscUtil.calculateSimpleStatus(5, 17));
        assertEquals(RollStatus.FAILURE, MiscUtil.calculateSimpleStatus(5, 16));
        assertEquals(RollStatus.FAILURE, MiscUtil.calculateSimpleStatus(5, 15));
        assertEquals(RollStatus.FAILURE, MiscUtil.calculateSimpleStatus(5, 14));
    }

    @Test
    void getArmors() {
        Map<String, Armor> armors = MiscUtil.getArmors();
        assertNotNull(armors);
        assertTrue(armors.size() > 0);
    }

    @Test
    void getShields() {
        Map<String, Shield> shields = MiscUtil.getShields();
        assertNotNull(shields);
        assertTrue(shields.size() > 0);
    }

    @Test
    void getSkills() {
        Map<String, Skill> skills = MiscUtil.getSkills();
        assertNotNull(skills);
        assertTrue(skills.size() > 0);
    }

    @Test
    void getWeapons() {
        Map<String, Weapon> weapons = MiscUtil.getWeapons();
        assertNotNull(weapons);
        assertTrue(weapons.size() > 0);
    }
}
