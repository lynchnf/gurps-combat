package norman.gurps.combat.service;

import norman.gurps.combat.TestHelper;
import norman.gurps.combat.model.ActionType;
import norman.gurps.combat.model.Battle;
import norman.gurps.combat.model.CombatDefense;
import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.DefenseType;
import norman.gurps.combat.model.GameChar;
import norman.gurps.combat.model.HealthStatus;
import norman.gurps.combat.model.NextStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CombatServiceTest {
    CombatService service;
    BattleService battleService;
    GameChar testGameChar1;
    GameChar testGameChar2;
    ArgumentCaptor<Battle> battleCaptor;

    @BeforeEach
    void setUp() {
        battleService = mock(BattleService.class);
        service = new CombatService(battleService);
        testGameChar1 = TestHelper.getGameChar1();
        testGameChar2 = TestHelper.getGameChar2();
        battleCaptor = ArgumentCaptor.forClass(Battle.class);
    }

    @Test
    void startCombat() {
        Battle battle = new Battle();
        battle.getCombatants().add(new Combatant(testGameChar1, new HashSet<>()));
        when(battleService.getBattle()).thenReturn(battle);

        service.startCombat();

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(1, battleCaptor.getValue().getNextStep().getRound());
        assertEquals(0, battleCaptor.getValue().getNextStep().getIndex());
        assertEquals(CombatPhase.BEGIN, battleCaptor.getValue().getNextStep().getCombatPhase());
    }

    @Test
    void nextStep_begin() {
        Battle battle = new Battle();
        battle.getCombatants().add(TestHelper.getCombatant(testGameChar1));
        battle.getCombatants().add(TestHelper.getCombatant(testGameChar2));
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.BEGIN);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.BEGIN, null, null, null, null, null, null, null, null, null,
                null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.PROMPT_FOR_ACTION, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(any(Battle.class), anyString());
    }

    @Test
    void nextStep_prompt_for_action() {
        Battle battle = new Battle();
        battle.getCombatants().add(TestHelper.getCombatant(testGameChar1));
        battle.getCombatants().add(TestHelper.getCombatant(testGameChar2));
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.PROMPT_FOR_ACTION);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.PROMPT_FOR_ACTION, null, null, null, null, null, null, null,
                null, null, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.RESOLVE_ACTION, nextStep.getCombatPhase());
        assertTrue(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(any(Battle.class), anyString());
    }

    @Test
    void nextStep_resolve_action() {
        Battle battle = new Battle();
        battle.getCombatants().add(TestHelper.getCombatant(testGameChar1));
        battle.getCombatants().add(TestHelper.getCombatant(testGameChar2));
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.RESOLVE_ACTION);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.RESOLVE_ACTION, ActionType.ATTACK, null, null, null, null,
                null, null, null, null, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.PROMPT_FOR_TARGET_AND_WEAPON, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(ActionType.ATTACK, battleCaptor.getValue().getCombatants().get(0).getActionType());
    }

    @Test
    void nextStep_prompt_for_target_and_weapon() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setActionType(ActionType.ATTACK);
        battle.getCombatants().add(combatant1);
        battle.getCombatants().add(TestHelper.getCombatant(testGameChar2));
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.PROMPT_FOR_TARGET_AND_WEAPON);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.PROMPT_FOR_TARGET_AND_WEAPON, null, null, null, null, null,
                null, null, null, null, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.RESOLVE_TARGET_AND_WEAPON, nextStep.getCombatPhase());
        assertTrue(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(any(Battle.class), anyString());
    }

    @Test
    void nextStep_resolve_target_and_weapon() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setActionType(ActionType.ATTACK);
        battle.getCombatants().add(combatant1);
        battle.getCombatants().add(TestHelper.getCombatant(testGameChar2));
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.RESOLVE_TARGET_AND_WEAPON);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.RESOLVE_TARGET_AND_WEAPON, null, "Grunt", "Broadsword",
                "swing", null, null, null, null, null, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.PROMPT_FOR_TO_HIT, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals("Grunt", battleCaptor.getValue().getCombatants().get(0).getTargetLabel());
        assertEquals("Broadsword", battleCaptor.getValue().getCombatants().get(0).getWeaponName());
        assertEquals("swing", battleCaptor.getValue().getCombatants().get(0).getWeaponModeName());
    }

    @Test
    void nextStep_prompt_for_to_hit() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setActionType(ActionType.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setWeaponModeName("swing");
        battle.getCombatants().add(combatant1);
        battle.getCombatants().add(TestHelper.getCombatant(testGameChar2));
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.PROMPT_FOR_TO_HIT);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.PROMPT_FOR_TO_HIT, null, null, null, null, null, null, null,
                null, null, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.RESOLVE_TO_HIT, nextStep.getCombatPhase());
        assertTrue(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(14, battleCaptor.getValue().getCombatants().get(0).getToHitEffectiveSkill());
    }

    @Test
    void nextStep_resolve_to_hit_fail() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setActionType(ActionType.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setWeaponModeName("swing");
        combatant1.setToHitEffectiveSkill(14);
        battle.getCombatants().add(combatant1);
        battle.getCombatants().add(TestHelper.getCombatant(testGameChar2));
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.RESOLVE_TO_HIT);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.RESOLVE_TO_HIT, null, null, null, null, 15, null, null, null,
                null, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.END, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(15, battleCaptor.getValue().getCombatants().get(0).getToHitRoll());
    }

    @Test
    void nextStep_resolve_to_hit_succeed() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setActionType(ActionType.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setWeaponModeName("swing");
        combatant1.setToHitEffectiveSkill(14);
        battle.getCombatants().add(combatant1);
        battle.getCombatants().add(TestHelper.getCombatant(testGameChar2));
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.RESOLVE_TO_HIT);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.RESOLVE_TO_HIT, null, null, null, null, 13, null, null, null,
                null, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.PROMPT_FOR_DEFENSE, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(13, battleCaptor.getValue().getCombatants().get(0).getToHitRoll());
    }

    @Test
    void nextStep_prompt_for_defense() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setActionType(ActionType.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setWeaponModeName("swing");
        combatant1.setToHitEffectiveSkill(14);
        combatant1.setToHitRoll(13);
        battle.getCombatants().add(combatant1);
        battle.getCombatants().add(TestHelper.getCombatant(testGameChar2));
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.PROMPT_FOR_DEFENSE);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.PROMPT_FOR_DEFENSE, null, null, null, null, null, null, null,
                null, null, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.RESOLVE_DEFENSE, nextStep.getCombatPhase());
        assertTrue(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(any(Battle.class), anyString());
    }

    @Test
    void nextStep_resolve_defense() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setActionType(ActionType.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setWeaponModeName("swing");
        combatant1.setToHitEffectiveSkill(14);
        combatant1.setToHitRoll(13);
        battle.getCombatants().add(combatant1);
        battle.getCombatants().add(TestHelper.getCombatant(testGameChar2));
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.RESOLVE_DEFENSE);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.RESOLVE_DEFENSE, null, null, null, null, null,
                DefenseType.BLOCK, "Medium Shield", null, null, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.PROMPT_FOR_TO_DEFEND, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(DefenseType.BLOCK,
                battleCaptor.getValue().getCombatants().get(1).getCombatDefenses().get(0).getDefenseType());
        assertEquals("Medium Shield",
                battleCaptor.getValue().getCombatants().get(1).getCombatDefenses().get(0).getDefendingItemName());
    }

    @Test
    void nextStep_prompt_for_to_defend() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setActionType(ActionType.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setWeaponModeName("swing");
        combatant1.setToHitEffectiveSkill(14);
        combatant1.setToHitRoll(13);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        CombatDefense combatDefense = new CombatDefense();
        combatDefense.setDefenseType(DefenseType.BLOCK);
        combatDefense.setDefendingItemName("Medium Shield");
        combatant2.getCombatDefenses().add(combatDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.PROMPT_FOR_TO_DEFEND);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.PROMPT_FOR_TO_DEFEND, null, null, null, null, null, null, null,
                null, null, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.RESOLVE_TO_DEFEND, nextStep.getCombatPhase());
        assertTrue(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(10,
                battleCaptor.getValue().getCombatants().get(1).getCombatDefenses().get(0).getToDefendEffectiveSkill());
    }

    @Test
    void nextStep_resolve_to_defend_succeed() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setActionType(ActionType.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setWeaponModeName("swing");
        combatant1.setToHitEffectiveSkill(14);
        combatant1.setToHitRoll(13);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        CombatDefense combatDefense = new CombatDefense();
        combatDefense.setDefenseType(DefenseType.BLOCK);
        combatDefense.setDefendingItemName("Medium Shield");
        combatDefense.setToDefendEffectiveSkill(10);
        combatant2.getCombatDefenses().add(combatDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.RESOLVE_TO_DEFEND);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.RESOLVE_TO_DEFEND, null, null, null, null, null, null, null, 9,
                null, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.END, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(9, battleCaptor.getValue().getCombatants().get(1).getCombatDefenses().get(0).getToDefendRoll());
    }

    @Test
    void nextStep_resolve_to_defend_fail() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setActionType(ActionType.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setWeaponModeName("swing");
        combatant1.setToHitEffectiveSkill(14);
        combatant1.setToHitRoll(13);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        CombatDefense combatDefense = new CombatDefense();
        combatDefense.setDefenseType(DefenseType.BLOCK);
        combatDefense.setDefendingItemName("Medium Shield");
        combatDefense.setToDefendEffectiveSkill(10);
        combatant2.getCombatDefenses().add(combatDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.RESOLVE_TO_DEFEND);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.RESOLVE_TO_DEFEND, null, null, null, null, null, null, null,
                11, null, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.PROMPT_FOR_DAMAGE, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(11, battleCaptor.getValue().getCombatants().get(1).getCombatDefenses().get(0).getToDefendRoll());
    }

    @Test
    void nextStep_prompt_for_damage() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setActionType(ActionType.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setWeaponModeName("swing");
        combatant1.setToHitEffectiveSkill(14);
        combatant1.setToHitRoll(13);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        CombatDefense combatDefense = new CombatDefense();
        combatDefense.setDefenseType(DefenseType.BLOCK);
        combatDefense.setDefendingItemName("Medium Shield");
        combatDefense.setToDefendEffectiveSkill(10);
        combatDefense.setToDefendRoll(11);
        combatant2.getCombatDefenses().add(combatDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.PROMPT_FOR_DAMAGE);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.PROMPT_FOR_DAMAGE, null, null, null, null, null, null, null,
                null, null, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.RESOLVE_DAMAGE, nextStep.getCombatPhase());
        assertTrue(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(2, battleCaptor.getValue().getCombatants().get(0).getDamageDice());
        assertEquals(1, battleCaptor.getValue().getCombatants().get(0).getDamageAdds());
    }

    @Test
    void nextStep_resolve_damage_no_damage() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setActionType(ActionType.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setWeaponModeName("swing");
        combatant1.setToHitEffectiveSkill(14);
        combatant1.setToHitRoll(13);
        combatant1.setDamageDice(2);
        combatant1.setDamageAdds(1);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        CombatDefense combatDefense = new CombatDefense();
        combatDefense.setDefenseType(DefenseType.BLOCK);
        combatDefense.setDefendingItemName("Medium Shield");
        combatDefense.setToDefendEffectiveSkill(10);
        combatDefense.setToDefendRoll(11);
        combatant2.getCombatDefenses().add(combatDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.RESOLVE_DAMAGE);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.RESOLVE_DAMAGE, null, null, null, null, null, null, null, null,
                2, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.END, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(2, battleCaptor.getValue().getCombatants().get(0).getForDamageRoll());
        assertEquals(0, battleCaptor.getValue().getCombatants().get(1).getCurrentDamage());
        assertEquals(0, battleCaptor.getValue().getCombatants().get(1).getNbrOfDeathChecksNeeded());
        assertEquals(HealthStatus.ALIVE, battleCaptor.getValue().getCombatants().get(1).getHealthStatus());
        assertEquals(3, battleCaptor.getValue().getCombatants().get(1).getCurrentMove());
    }

    @Test
    void nextStep_resolve_damage_alive() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setActionType(ActionType.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setWeaponModeName("swing");
        combatant1.setToHitEffectiveSkill(14);
        combatant1.setToHitRoll(13);
        combatant1.setDamageDice(2);
        combatant1.setDamageAdds(1);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        CombatDefense combatDefense = new CombatDefense();
        combatDefense.setDefenseType(DefenseType.BLOCK);
        combatDefense.setDefendingItemName("Medium Shield");
        combatDefense.setToDefendEffectiveSkill(10);
        combatDefense.setToDefendRoll(11);
        combatant2.getCombatDefenses().add(combatDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.RESOLVE_DAMAGE);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.RESOLVE_DAMAGE, null, null, null, null, null, null, null, null,
                4, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.END, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(4, battleCaptor.getValue().getCombatants().get(0).getForDamageRoll());
        assertEquals(3, battleCaptor.getValue().getCombatants().get(1).getCurrentDamage());
        assertEquals(0, battleCaptor.getValue().getCombatants().get(1).getNbrOfDeathChecksNeeded());
        assertEquals(HealthStatus.ALIVE, battleCaptor.getValue().getCombatants().get(1).getHealthStatus());
        assertEquals(3, battleCaptor.getValue().getCombatants().get(1).getCurrentMove());
    }

    @Test
    void nextStep_resolve_damage_reeling() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setActionType(ActionType.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setWeaponModeName("swing");
        combatant1.setToHitEffectiveSkill(14);
        combatant1.setToHitRoll(13);
        combatant1.setDamageDice(2);
        combatant1.setDamageAdds(1);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        CombatDefense combatDefense = new CombatDefense();
        combatDefense.setDefenseType(DefenseType.BLOCK);
        combatDefense.setDefendingItemName("Medium Shield");
        combatDefense.setToDefendEffectiveSkill(10);
        combatDefense.setToDefendRoll(11);
        combatant2.getCombatDefenses().add(combatDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.RESOLVE_DAMAGE);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.RESOLVE_DAMAGE, null, null, null, null, null, null, null, null,
                7, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.END, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(7, battleCaptor.getValue().getCombatants().get(0).getForDamageRoll());
        assertEquals(7, battleCaptor.getValue().getCombatants().get(1).getCurrentDamage());
        assertEquals(0, battleCaptor.getValue().getCombatants().get(1).getNbrOfDeathChecksNeeded());
        assertEquals(HealthStatus.REELING, battleCaptor.getValue().getCombatants().get(1).getHealthStatus());
        assertEquals(2, battleCaptor.getValue().getCombatants().get(1).getCurrentMove());
    }

    @Test
    void nextStep_resolve_damage_barely() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setActionType(ActionType.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setWeaponModeName("swing");
        combatant1.setToHitEffectiveSkill(14);
        combatant1.setToHitRoll(13);
        combatant1.setDamageDice(2);
        combatant1.setDamageAdds(1);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        CombatDefense combatDefense = new CombatDefense();
        combatDefense.setDefenseType(DefenseType.BLOCK);
        combatDefense.setDefendingItemName("Medium Shield");
        combatDefense.setToDefendEffectiveSkill(10);
        combatDefense.setToDefendRoll(11);
        combatant2.getCombatDefenses().add(combatDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.RESOLVE_DAMAGE);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.RESOLVE_DAMAGE, null, null, null, null, null, null, null, null,
                10, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.END, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(10, battleCaptor.getValue().getCombatants().get(0).getForDamageRoll());
        assertEquals(12, battleCaptor.getValue().getCombatants().get(1).getCurrentDamage());
        assertEquals(0, battleCaptor.getValue().getCombatants().get(1).getNbrOfDeathChecksNeeded());
        assertEquals(HealthStatus.BARELY, battleCaptor.getValue().getCombatants().get(1).getHealthStatus());
        assertEquals(2, battleCaptor.getValue().getCombatants().get(1).getCurrentMove());
    }

    @Test
    void nextStep_resolve_damage_almost() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setActionType(ActionType.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setWeaponModeName("swing");
        combatant1.setToHitEffectiveSkill(14);
        combatant1.setToHitRoll(13);
        combatant1.setDamageDice(2);
        combatant1.setDamageAdds(1);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        CombatDefense combatDefense = new CombatDefense();
        combatDefense.setDefenseType(DefenseType.BLOCK);
        combatDefense.setDefendingItemName("Medium Shield");
        combatDefense.setToDefendEffectiveSkill(10);
        combatDefense.setToDefendRoll(11);
        combatant2.getCombatDefenses().add(combatDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.RESOLVE_DAMAGE);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.RESOLVE_DAMAGE, null, null, null, null, null, null, null, null,
                16, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.PROMPT_FOR_DEATH_CHECK, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(16, battleCaptor.getValue().getCombatants().get(0).getForDamageRoll());
        assertEquals(21, battleCaptor.getValue().getCombatants().get(1).getCurrentDamage());
        assertEquals(1, battleCaptor.getValue().getCombatants().get(1).getNbrOfDeathChecksNeeded());
        assertEquals(HealthStatus.ALMOST, battleCaptor.getValue().getCombatants().get(1).getHealthStatus());
        assertEquals(2, battleCaptor.getValue().getCombatants().get(1).getCurrentMove());
    }

    @Test
    void nextStep_resolve_damage_almost2() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setActionType(ActionType.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setWeaponModeName("swing");
        combatant1.setToHitEffectiveSkill(14);
        combatant1.setToHitRoll(13);
        combatant1.setDamageDice(2);
        combatant1.setDamageAdds(1);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        CombatDefense combatDefense = new CombatDefense();
        combatDefense.setDefenseType(DefenseType.BLOCK);
        combatDefense.setDefendingItemName("Medium Shield");
        combatDefense.setToDefendEffectiveSkill(10);
        combatDefense.setToDefendRoll(11);
        combatant2.getCombatDefenses().add(combatDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.RESOLVE_DAMAGE);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.RESOLVE_DAMAGE, null, null, null, null, null, null, null, null,
                23, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.PROMPT_FOR_DEATH_CHECK, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(23, battleCaptor.getValue().getCombatants().get(0).getForDamageRoll());
        assertEquals(31, battleCaptor.getValue().getCombatants().get(1).getCurrentDamage());
        assertEquals(2, battleCaptor.getValue().getCombatants().get(1).getNbrOfDeathChecksNeeded());
        assertEquals(HealthStatus.ALMOST2, battleCaptor.getValue().getCombatants().get(1).getHealthStatus());
        assertEquals(2, battleCaptor.getValue().getCombatants().get(1).getCurrentMove());
    }

    @Test
    void nextStep_prompt_for_death_check() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setActionType(ActionType.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setWeaponModeName("swing");
        combatant1.setToHitEffectiveSkill(14);
        combatant1.setToHitRoll(13);
        combatant1.setDamageDice(2);
        combatant1.setDamageAdds(1);
        combatant1.setForDamageRoll(16);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        combatant2.setCurrentDamage(21);
        combatant2.setNbrOfDeathChecksNeeded(1);
        combatant2.setHealthStatus(HealthStatus.ALMOST);
        combatant2.setCurrentMove(2);
        CombatDefense combatDefense = new CombatDefense();
        combatDefense.setDefenseType(DefenseType.BLOCK);
        combatDefense.setDefendingItemName("Medium Shield");
        combatDefense.setToDefendEffectiveSkill(10);
        combatDefense.setToDefendRoll(11);
        combatant2.getCombatDefenses().add(combatDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.PROMPT_FOR_DEATH_CHECK);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.PROMPT_FOR_DEATH_CHECK, null, null, null, null, null, null,
                null, null, null, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.RESOLVE_DEATH_CHECK, nextStep.getCombatPhase());
        assertTrue(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(1, battleCaptor.getValue().getCombatants().get(1).getNbrOfDeathChecksNeeded());
        assertFalse(battleCaptor.getValue().getCombatants().get(1).getDeathCheckFailed());
    }

    @Test
    void nextStep_resolve_death_check_fail() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setActionType(ActionType.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setWeaponModeName("swing");
        combatant1.setToHitEffectiveSkill(14);
        combatant1.setToHitRoll(13);
        combatant1.setDamageDice(2);
        combatant1.setDamageAdds(1);
        combatant1.setForDamageRoll(23);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        combatant2.setCurrentDamage(31);
        combatant2.setNbrOfDeathChecksNeeded(2);
        combatant2.setHealthStatus(HealthStatus.ALMOST2);
        combatant2.setCurrentMove(2);
        CombatDefense combatDefense = new CombatDefense();
        combatDefense.setDefenseType(DefenseType.BLOCK);
        combatDefense.setDefendingItemName("Medium Shield");
        combatDefense.setToDefendEffectiveSkill(10);
        combatDefense.setToDefendRoll(11);
        combatant2.getCombatDefenses().add(combatDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.RESOLVE_DEATH_CHECK);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.RESOLVE_DEATH_CHECK, null, null, null, null, null, null, null,
                null, null, 11, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.END, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(0, battleCaptor.getValue().getCombatants().get(1).getNbrOfDeathChecksNeeded());
        assertTrue(battleCaptor.getValue().getCombatants().get(1).getDeathCheckFailed());
        assertEquals(HealthStatus.DEAD, battleCaptor.getValue().getCombatants().get(1).getHealthStatus());
        assertEquals(0, battleCaptor.getValue().getCombatants().get(1).getCurrentMove());
    }

    @Test
    void nextStep_resolve_death_check_succeed() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setActionType(ActionType.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setWeaponModeName("swing");
        combatant1.setToHitEffectiveSkill(14);
        combatant1.setToHitRoll(13);
        combatant1.setDamageDice(2);
        combatant1.setDamageAdds(1);
        combatant1.setForDamageRoll(23);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        combatant2.setCurrentDamage(31);
        combatant2.setNbrOfDeathChecksNeeded(2);
        combatant2.setHealthStatus(HealthStatus.ALMOST2);
        combatant2.setCurrentMove(2);
        CombatDefense combatDefense = new CombatDefense();
        combatDefense.setDefenseType(DefenseType.BLOCK);
        combatDefense.setDefendingItemName("Medium Shield");
        combatDefense.setToDefendEffectiveSkill(10);
        combatDefense.setToDefendRoll(11);
        combatant2.getCombatDefenses().add(combatDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.RESOLVE_DEATH_CHECK);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.RESOLVE_DEATH_CHECK, null, null, null, null, null, null, null,
                null, null, 9, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.PROMPT_FOR_DEATH_CHECK, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(1, battleCaptor.getValue().getCombatants().get(1).getNbrOfDeathChecksNeeded());
        assertFalse(battleCaptor.getValue().getCombatants().get(1).getDeathCheckFailed());
        assertEquals(HealthStatus.ALMOST2, battleCaptor.getValue().getCombatants().get(1).getHealthStatus());
        assertEquals(2, battleCaptor.getValue().getCombatants().get(1).getCurrentMove());
    }

    @Test
    void nextStep_resolve_death_check_succeed2() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setActionType(ActionType.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setWeaponModeName("swing");
        combatant1.setToHitEffectiveSkill(14);
        combatant1.setToHitRoll(13);
        combatant1.setDamageDice(2);
        combatant1.setDamageAdds(1);
        combatant1.setForDamageRoll(23);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        combatant2.setCurrentDamage(31);
        combatant2.setNbrOfDeathChecksNeeded(1);
        combatant2.setHealthStatus(HealthStatus.ALMOST2);
        combatant2.setCurrentMove(2);
        CombatDefense combatDefense = new CombatDefense();
        combatDefense.setDefenseType(DefenseType.BLOCK);
        combatDefense.setDefendingItemName("Medium Shield");
        combatDefense.setToDefendEffectiveSkill(10);
        combatDefense.setToDefendRoll(11);
        combatant2.getCombatDefenses().add(combatDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.RESOLVE_DEATH_CHECK);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.RESOLVE_DEATH_CHECK, null, null, null, null, null, null, null,
                null, null, 9, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(CombatPhase.END, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(0, battleCaptor.getValue().getCombatants().get(1).getNbrOfDeathChecksNeeded());
        assertFalse(battleCaptor.getValue().getCombatants().get(1).getDeathCheckFailed());
        assertEquals(HealthStatus.ALMOST2, battleCaptor.getValue().getCombatants().get(1).getHealthStatus());
        assertEquals(2, battleCaptor.getValue().getCombatants().get(1).getCurrentMove());
    }

    @Test
    void nextStep_prompt_for_unconsciousness_check() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        combatant2.setCurrentDamage(31);
        combatant2.setHealthStatus(HealthStatus.ALMOST2);
        combatant2.setCurrentMove(2);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(1);
        nextStep1.setCombatPhase(CombatPhase.PROMPT_FOR_UNCONSCIOUSNESS_CHECK);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.PROMPT_FOR_UNCONSCIOUSNESS_CHECK, null, null, null, null, null,
                null, null, null, null, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(1, nextStep.getIndex());
        assertEquals(CombatPhase.RESOLVE_UNCONSCIOUSNESS_CHECK, nextStep.getCombatPhase());
        assertTrue(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
    }

    @Test
    void nextStep_resolve_unconsciousness_check() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        combatant2.setCurrentDamage(31);
        combatant2.setHealthStatus(HealthStatus.ALMOST2);
        combatant2.setCurrentMove(2);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(1);
        nextStep1.setCombatPhase(CombatPhase.RESOLVE_UNCONSCIOUSNESS_CHECK);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.RESOLVE_UNCONSCIOUSNESS_CHECK, null, null, null, null, null,
                null, null, null, null, null, 11);

        assertEquals(1, nextStep.getRound());
        assertEquals(1, nextStep.getIndex());
        assertEquals(CombatPhase.END, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertTrue(battleCaptor.getValue().getCombatants().get(1).getUnconsciousnessCheckFailed());
        assertEquals(HealthStatus.UNCONSCIOUS, battleCaptor.getValue().getCombatants().get(1).getHealthStatus());
        assertEquals(0, battleCaptor.getValue().getCombatants().get(1).getCurrentMove());
    }

    @Test
    void nextStep_end() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setActionType(ActionType.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setWeaponModeName("swing");
        combatant1.setToHitEffectiveSkill(14);
        combatant1.setToHitRoll(13);
        combatant1.setDamageDice(2);
        combatant1.setDamageAdds(1);
        combatant1.setForDamageRoll(23);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        combatant2.setCurrentDamage(31);
        combatant2.setNbrOfDeathChecksNeeded(0);
        combatant2.setHealthStatus(HealthStatus.ALMOST2);
        combatant2.setCurrentMove(2);
        CombatDefense combatDefense = new CombatDefense();
        combatDefense.setDefenseType(DefenseType.BLOCK);
        combatDefense.setDefendingItemName("Medium Shield");
        combatDefense.setToDefendEffectiveSkill(10);
        combatDefense.setToDefendRoll(11);
        combatant2.getCombatDefenses().add(combatDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.END);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.END, null, null, null, null, null, null, null, null, null,
                null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(1, nextStep.getIndex());
        assertEquals(CombatPhase.BEGIN, nextStep.getCombatPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNull(nextStep.getMessage());

        verify(battleService).updateBattle(any(Battle.class), anyString());
    }
}
