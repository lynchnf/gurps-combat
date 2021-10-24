package norman.gurps.util;

import norman.gurps.equipment.Armor;
import norman.gurps.equipment.Shield;
import norman.gurps.equipment.Weapon;
import norman.gurps.skill.ControllingAttribute;
import norman.gurps.skill.DifficultyLevel;
import norman.gurps.skill.Skill;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MiscUtilTest {
    @Test
    void rollDice() {
        Random mockRandom = mock(Random.class);
        Mockito.when(mockRandom.nextInt(6)).thenReturn(4);
        MiscUtil.setRandom(mockRandom);

        assertEquals(5, MiscUtil.rollDice(1));
        assertEquals(10, MiscUtil.rollDice(2));
        assertEquals(15, MiscUtil.rollDice(3));

        assertEquals(9, MiscUtil.rollDice(2, -1));
        assertEquals(11, MiscUtil.rollDice(2, 1));
        assertEquals(13, MiscUtil.rollDice(2, 3));
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
    void calculateSkillLevel() {
        assertEquals(10, MiscUtil.calculateSkillLevel(10, DifficultyLevel.EASY, 1));
        assertEquals(11, MiscUtil.calculateSkillLevel(10, DifficultyLevel.EASY, 2));
        assertEquals(12, MiscUtil.calculateSkillLevel(10, DifficultyLevel.EASY, 4));
        assertEquals(13, MiscUtil.calculateSkillLevel(10, DifficultyLevel.EASY, 8));
        assertEquals(14, MiscUtil.calculateSkillLevel(10, DifficultyLevel.EASY, 12));

        assertEquals(9, MiscUtil.calculateSkillLevel(10, DifficultyLevel.AVERAGE, 1));
        assertEquals(10, MiscUtil.calculateSkillLevel(10, DifficultyLevel.AVERAGE, 2));
        assertEquals(11, MiscUtil.calculateSkillLevel(10, DifficultyLevel.AVERAGE, 4));
        assertEquals(12, MiscUtil.calculateSkillLevel(10, DifficultyLevel.AVERAGE, 8));
        assertEquals(13, MiscUtil.calculateSkillLevel(10, DifficultyLevel.AVERAGE, 12));

        assertEquals(8, MiscUtil.calculateSkillLevel(10, DifficultyLevel.HARD, 1));
        assertEquals(9, MiscUtil.calculateSkillLevel(10, DifficultyLevel.HARD, 2));
        assertEquals(10, MiscUtil.calculateSkillLevel(10, DifficultyLevel.HARD, 4));
        assertEquals(11, MiscUtil.calculateSkillLevel(10, DifficultyLevel.HARD, 8));
        assertEquals(12, MiscUtil.calculateSkillLevel(10, DifficultyLevel.HARD, 12));
    }

    @Test
    void calculateSkillLevelHighAttribute() {
        assertEquals(18, MiscUtil.calculateSkillLevel(20, DifficultyLevel.HARD, 1));

        assertEquals(18, MiscUtil.calculateSkillLevel(25, DifficultyLevel.HARD, 1));
    }

    @Test
    void calculateSkillLevelControllingAttribute() {
        assertEquals(9, MiscUtil.calculateSkillLevel(ControllingAttribute.ST, DifficultyLevel.HARD, 1, 11, 12, 13, 14));
        assertEquals(10,
                MiscUtil.calculateSkillLevel(ControllingAttribute.DX, DifficultyLevel.HARD, 1, 11, 12, 13, 14));
        assertEquals(11,
                MiscUtil.calculateSkillLevel(ControllingAttribute.IQ, DifficultyLevel.HARD, 1, 11, 12, 13, 14));
        assertEquals(12,
                MiscUtil.calculateSkillLevel(ControllingAttribute.HT, DifficultyLevel.HARD, 1, 11, 12, 13, 14));
    }

    @Test
    void getArmors() {
        Map<String, Armor> armors = MiscUtil.getArmors();
        assertNotNull(armors);
        assertFalse(armors.isEmpty());
    }

    @Test
    void getShields() {
        Map<String, Shield> shields = MiscUtil.getShields();
        assertNotNull(shields);
        assertFalse(shields.isEmpty());
    }

    @Test
    void getSkills() {
        Map<String, Skill> skills = MiscUtil.getSkills();
        assertNotNull(skills);
        assertFalse(skills.isEmpty());
    }

    @Test
    void getWeapons() {
        Map<String, Weapon> weapons = MiscUtil.getWeapons();
        assertNotNull(weapons);
        assertFalse(weapons.isEmpty());
    }
}
