package norman.gurps.combat.service;

import norman.gurps.combat.TestHelper;
import norman.gurps.combat.model.Action;
import norman.gurps.combat.model.ActiveDefense;
import norman.gurps.combat.model.Battle;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.DefenseType;
import norman.gurps.combat.model.GameChar;
import norman.gurps.combat.model.HealthStatus;
import norman.gurps.combat.model.NextStep;
import norman.gurps.combat.model.Phase;
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
        assertEquals(Phase.BEGIN, battleCaptor.getValue().getNextStep().getPhase());
    }

    @Test
    void nextStep_begin() {
        Battle battle = new Battle();
        battle.getCombatants().add(TestHelper.getCombatant(testGameChar1));
        battle.getCombatants().add(TestHelper.getCombatant(testGameChar2));
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setPhase(Phase.BEGIN);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(Phase.BEGIN, null, null, null, null, null, null, null, null, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(Phase.PROMPT_FOR_ACTION, nextStep.getPhase());
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
        nextStep1.setPhase(Phase.PROMPT_FOR_ACTION);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(Phase.PROMPT_FOR_ACTION, null, null, null, null, null, null, null, null,
                null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(Phase.RESOLVE_ACTION, nextStep.getPhase());
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
        nextStep1.setPhase(Phase.RESOLVE_ACTION);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(Phase.RESOLVE_ACTION, Action.ATTACK, null, null, null, null, null, null,
                null, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(Phase.PROMPT_FOR_TARGET_AND_WEAPON, nextStep.getPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(Action.ATTACK, battleCaptor.getValue().getCombatants().get(0).getAction());
    }

    @Test
    void nextStep_prompt_for_target_and_weapon() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setAction(Action.ATTACK);
        battle.getCombatants().add(combatant1);
        battle.getCombatants().add(TestHelper.getCombatant(testGameChar2));
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setPhase(Phase.PROMPT_FOR_TARGET_AND_WEAPON);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(Phase.PROMPT_FOR_TARGET_AND_WEAPON, null, null, null, null, null, null,
                null, null, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(Phase.RESOLVE_TARGET_AND_WEAPON, nextStep.getPhase());
        assertTrue(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(any(Battle.class), anyString());
    }

    @Test
    void nextStep_resolve_target_and_weapon() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setAction(Action.ATTACK);
        battle.getCombatants().add(combatant1);
        battle.getCombatants().add(TestHelper.getCombatant(testGameChar2));
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setPhase(Phase.RESOLVE_TARGET_AND_WEAPON);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(Phase.RESOLVE_TARGET_AND_WEAPON, null, "Grunt", "Broadsword", "swing",
                null, null, null, null, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(Phase.PROMPT_FOR_TO_HIT, nextStep.getPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals("Grunt", battleCaptor.getValue().getCombatants().get(0).getTargetLabel());
        assertEquals("Broadsword", battleCaptor.getValue().getCombatants().get(0).getWeaponName());
        assertEquals("swing", battleCaptor.getValue().getCombatants().get(0).getModeName());
    }

    @Test
    void nextStep_prompt_for_to_hit() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setAction(Action.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setModeName("swing");
        battle.getCombatants().add(combatant1);
        battle.getCombatants().add(TestHelper.getCombatant(testGameChar2));
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setPhase(Phase.PROMPT_FOR_TO_HIT);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(Phase.PROMPT_FOR_TO_HIT, null, null, null, null, null, null, null, null,
                null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(Phase.RESOLVE_TO_HIT, nextStep.getPhase());
        assertTrue(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(14, battleCaptor.getValue().getCombatants().get(0).getEffectiveSkillToHit());
    }

    @Test
    void nextStep_resolve_to_hit_fail() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setAction(Action.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setModeName("swing");
        combatant1.setEffectiveSkillToHit(14);
        battle.getCombatants().add(combatant1);
        battle.getCombatants().add(TestHelper.getCombatant(testGameChar2));
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setPhase(Phase.RESOLVE_TO_HIT);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(Phase.RESOLVE_TO_HIT, null, null, null, null, 15, null, null, null, null,
                null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(Phase.END, nextStep.getPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(15, battleCaptor.getValue().getCombatants().get(0).getRollToHit());
    }

    @Test
    void nextStep_resolve_to_hit_succeed() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setAction(Action.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setModeName("swing");
        combatant1.setEffectiveSkillToHit(14);
        battle.getCombatants().add(combatant1);
        battle.getCombatants().add(TestHelper.getCombatant(testGameChar2));
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setPhase(Phase.RESOLVE_TO_HIT);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(Phase.RESOLVE_TO_HIT, null, null, null, null, 13, null, null, null, null,
                null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(Phase.PROMPT_FOR_DEFENSE, nextStep.getPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(13, battleCaptor.getValue().getCombatants().get(0).getRollToHit());
    }

    @Test
    void nextStep_prompt_for_defense() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setAction(Action.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setModeName("swing");
        combatant1.setEffectiveSkillToHit(14);
        combatant1.setRollToHit(13);
        battle.getCombatants().add(combatant1);
        battle.getCombatants().add(TestHelper.getCombatant(testGameChar2));
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setPhase(Phase.PROMPT_FOR_DEFENSE);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(Phase.PROMPT_FOR_DEFENSE, null, null, null, null, null, null, null, null,
                null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(Phase.RESOLVE_DEFENSE, nextStep.getPhase());
        assertTrue(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(any(Battle.class), anyString());
    }

    @Test
    void nextStep_resolve_defense() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setAction(Action.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setModeName("swing");
        combatant1.setEffectiveSkillToHit(14);
        combatant1.setRollToHit(13);
        battle.getCombatants().add(combatant1);
        battle.getCombatants().add(TestHelper.getCombatant(testGameChar2));
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setPhase(Phase.RESOLVE_DEFENSE);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(Phase.RESOLVE_DEFENSE, null, null, null, null, null, DefenseType.BLOCK,
                "Medium Shield", null, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(Phase.PROMPT_FOR_TO_DEFEND, nextStep.getPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(DefenseType.BLOCK,
                battleCaptor.getValue().getCombatants().get(1).getActiveDefenses().get(0).getDefenseType());
        assertEquals("Medium Shield",
                battleCaptor.getValue().getCombatants().get(1).getActiveDefenses().get(0).getDefendingItemName());
    }

    @Test
    void nextStep_prompt_for_to_defend() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setAction(Action.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setModeName("swing");
        combatant1.setEffectiveSkillToHit(14);
        combatant1.setRollToHit(13);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        ActiveDefense activeDefense = new ActiveDefense();
        activeDefense.setDefenseType(DefenseType.BLOCK);
        activeDefense.setDefendingItemName("Medium Shield");
        combatant2.getActiveDefenses().add(activeDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setPhase(Phase.PROMPT_FOR_TO_DEFEND);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(Phase.PROMPT_FOR_TO_DEFEND, null, null, null, null, null, null, null, null,
                null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(Phase.RESOLVE_TO_DEFEND, nextStep.getPhase());
        assertTrue(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(10,
                battleCaptor.getValue().getCombatants().get(1).getActiveDefenses().get(0).getEffectiveSkillToDefend());
    }

    @Test
    void nextStep_resolve_to_defend_succeed() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setAction(Action.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setModeName("swing");
        combatant1.setEffectiveSkillToHit(14);
        combatant1.setRollToHit(13);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        ActiveDefense activeDefense = new ActiveDefense();
        activeDefense.setDefenseType(DefenseType.BLOCK);
        activeDefense.setDefendingItemName("Medium Shield");
        activeDefense.setEffectiveSkillToDefend(10);
        combatant2.getActiveDefenses().add(activeDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setPhase(Phase.RESOLVE_TO_DEFEND);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(Phase.RESOLVE_TO_DEFEND, null, null, null, null, null, null, null, 9, null,
                null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(Phase.END, nextStep.getPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(9, battleCaptor.getValue().getCombatants().get(1).getActiveDefenses().get(0).getRollToDefend());
    }

    @Test
    void nextStep_resolve_to_defend_fail() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setAction(Action.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setModeName("swing");
        combatant1.setEffectiveSkillToHit(14);
        combatant1.setRollToHit(13);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        ActiveDefense activeDefense = new ActiveDefense();
        activeDefense.setDefenseType(DefenseType.BLOCK);
        activeDefense.setDefendingItemName("Medium Shield");
        activeDefense.setEffectiveSkillToDefend(10);
        combatant2.getActiveDefenses().add(activeDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setPhase(Phase.RESOLVE_TO_DEFEND);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(Phase.RESOLVE_TO_DEFEND, null, null, null, null, null, null, null, 11,
                null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(Phase.PROMPT_FOR_DAMAGE, nextStep.getPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(11, battleCaptor.getValue().getCombatants().get(1).getActiveDefenses().get(0).getRollToDefend());
    }

    @Test
    void nextStep_prompt_for_damage() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setAction(Action.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setModeName("swing");
        combatant1.setEffectiveSkillToHit(14);
        combatant1.setRollToHit(13);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        ActiveDefense activeDefense = new ActiveDefense();
        activeDefense.setDefenseType(DefenseType.BLOCK);
        activeDefense.setDefendingItemName("Medium Shield");
        activeDefense.setEffectiveSkillToDefend(10);
        activeDefense.setRollToDefend(11);
        combatant2.getActiveDefenses().add(activeDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setPhase(Phase.PROMPT_FOR_DAMAGE);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(Phase.PROMPT_FOR_DAMAGE, null, null, null, null, null, null, null, null,
                null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(Phase.RESOLVE_DAMAGE, nextStep.getPhase());
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
        combatant1.setAction(Action.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setModeName("swing");
        combatant1.setEffectiveSkillToHit(14);
        combatant1.setRollToHit(13);
        combatant1.setDamageDice(2);
        combatant1.setDamageAdds(1);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        ActiveDefense activeDefense = new ActiveDefense();
        activeDefense.setDefenseType(DefenseType.BLOCK);
        activeDefense.setDefendingItemName("Medium Shield");
        activeDefense.setEffectiveSkillToDefend(10);
        activeDefense.setRollToDefend(11);
        combatant2.getActiveDefenses().add(activeDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setPhase(Phase.RESOLVE_DAMAGE);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(Phase.RESOLVE_DAMAGE, null, null, null, null, null, null, null, null, 2,
                null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(Phase.END, nextStep.getPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(2, battleCaptor.getValue().getCombatants().get(0).getRollForDamage());
        assertEquals(0, battleCaptor.getValue().getCombatants().get(1).getCurrentDamage());
        assertEquals(0, battleCaptor.getValue().getCombatants().get(1).getNbrOfDeathChecksNeeded());
        assertEquals(HealthStatus.ALIVE, battleCaptor.getValue().getCombatants().get(1).getHealthStatus());
        assertEquals(3, battleCaptor.getValue().getCombatants().get(1).getCurrentMove());
    }

    @Test
    void nextStep_resolve_damage_alive() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setAction(Action.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setModeName("swing");
        combatant1.setEffectiveSkillToHit(14);
        combatant1.setRollToHit(13);
        combatant1.setDamageDice(2);
        combatant1.setDamageAdds(1);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        ActiveDefense activeDefense = new ActiveDefense();
        activeDefense.setDefenseType(DefenseType.BLOCK);
        activeDefense.setDefendingItemName("Medium Shield");
        activeDefense.setEffectiveSkillToDefend(10);
        activeDefense.setRollToDefend(11);
        combatant2.getActiveDefenses().add(activeDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setPhase(Phase.RESOLVE_DAMAGE);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(Phase.RESOLVE_DAMAGE, null, null, null, null, null, null, null, null, 4,
                null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(Phase.END, nextStep.getPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(4, battleCaptor.getValue().getCombatants().get(0).getRollForDamage());
        assertEquals(3, battleCaptor.getValue().getCombatants().get(1).getCurrentDamage());
        assertEquals(0, battleCaptor.getValue().getCombatants().get(1).getNbrOfDeathChecksNeeded());
        assertEquals(HealthStatus.ALIVE, battleCaptor.getValue().getCombatants().get(1).getHealthStatus());
        assertEquals(3, battleCaptor.getValue().getCombatants().get(1).getCurrentMove());
    }

    @Test
    void nextStep_resolve_damage_reeling() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setAction(Action.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setModeName("swing");
        combatant1.setEffectiveSkillToHit(14);
        combatant1.setRollToHit(13);
        combatant1.setDamageDice(2);
        combatant1.setDamageAdds(1);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        ActiveDefense activeDefense = new ActiveDefense();
        activeDefense.setDefenseType(DefenseType.BLOCK);
        activeDefense.setDefendingItemName("Medium Shield");
        activeDefense.setEffectiveSkillToDefend(10);
        activeDefense.setRollToDefend(11);
        combatant2.getActiveDefenses().add(activeDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setPhase(Phase.RESOLVE_DAMAGE);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(Phase.RESOLVE_DAMAGE, null, null, null, null, null, null, null, null, 7,
                null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(Phase.END, nextStep.getPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(7, battleCaptor.getValue().getCombatants().get(0).getRollForDamage());
        assertEquals(7, battleCaptor.getValue().getCombatants().get(1).getCurrentDamage());
        assertEquals(0, battleCaptor.getValue().getCombatants().get(1).getNbrOfDeathChecksNeeded());
        assertEquals(HealthStatus.REELING, battleCaptor.getValue().getCombatants().get(1).getHealthStatus());
        assertEquals(2, battleCaptor.getValue().getCombatants().get(1).getCurrentMove());
    }

    @Test
    void nextStep_resolve_damage_barely() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setAction(Action.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setModeName("swing");
        combatant1.setEffectiveSkillToHit(14);
        combatant1.setRollToHit(13);
        combatant1.setDamageDice(2);
        combatant1.setDamageAdds(1);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        ActiveDefense activeDefense = new ActiveDefense();
        activeDefense.setDefenseType(DefenseType.BLOCK);
        activeDefense.setDefendingItemName("Medium Shield");
        activeDefense.setEffectiveSkillToDefend(10);
        activeDefense.setRollToDefend(11);
        combatant2.getActiveDefenses().add(activeDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setPhase(Phase.RESOLVE_DAMAGE);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(Phase.RESOLVE_DAMAGE, null, null, null, null, null, null, null, null, 10,
                null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(Phase.END, nextStep.getPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(10, battleCaptor.getValue().getCombatants().get(0).getRollForDamage());
        assertEquals(12, battleCaptor.getValue().getCombatants().get(1).getCurrentDamage());
        assertEquals(0, battleCaptor.getValue().getCombatants().get(1).getNbrOfDeathChecksNeeded());
        assertEquals(HealthStatus.BARELY, battleCaptor.getValue().getCombatants().get(1).getHealthStatus());
        assertEquals(2, battleCaptor.getValue().getCombatants().get(1).getCurrentMove());
    }

    @Test
    void nextStep_resolve_damage_almost() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setAction(Action.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setModeName("swing");
        combatant1.setEffectiveSkillToHit(14);
        combatant1.setRollToHit(13);
        combatant1.setDamageDice(2);
        combatant1.setDamageAdds(1);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        ActiveDefense activeDefense = new ActiveDefense();
        activeDefense.setDefenseType(DefenseType.BLOCK);
        activeDefense.setDefendingItemName("Medium Shield");
        activeDefense.setEffectiveSkillToDefend(10);
        activeDefense.setRollToDefend(11);
        combatant2.getActiveDefenses().add(activeDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setPhase(Phase.RESOLVE_DAMAGE);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(Phase.RESOLVE_DAMAGE, null, null, null, null, null, null, null, null, 16,
                null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(Phase.PROMPT_FOR_DEATH_CHECK, nextStep.getPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(16, battleCaptor.getValue().getCombatants().get(0).getRollForDamage());
        assertEquals(21, battleCaptor.getValue().getCombatants().get(1).getCurrentDamage());
        assertEquals(1, battleCaptor.getValue().getCombatants().get(1).getNbrOfDeathChecksNeeded());
        assertEquals(HealthStatus.ALMOST, battleCaptor.getValue().getCombatants().get(1).getHealthStatus());
        assertEquals(2, battleCaptor.getValue().getCombatants().get(1).getCurrentMove());
    }

    @Test
    void nextStep_resolve_damage_almost2() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setAction(Action.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setModeName("swing");
        combatant1.setEffectiveSkillToHit(14);
        combatant1.setRollToHit(13);
        combatant1.setDamageDice(2);
        combatant1.setDamageAdds(1);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        ActiveDefense activeDefense = new ActiveDefense();
        activeDefense.setDefenseType(DefenseType.BLOCK);
        activeDefense.setDefendingItemName("Medium Shield");
        activeDefense.setEffectiveSkillToDefend(10);
        activeDefense.setRollToDefend(11);
        combatant2.getActiveDefenses().add(activeDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setPhase(Phase.RESOLVE_DAMAGE);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(Phase.RESOLVE_DAMAGE, null, null, null, null, null, null, null, null, 23,
                null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(Phase.PROMPT_FOR_DEATH_CHECK, nextStep.getPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(23, battleCaptor.getValue().getCombatants().get(0).getRollForDamage());
        assertEquals(31, battleCaptor.getValue().getCombatants().get(1).getCurrentDamage());
        assertEquals(2, battleCaptor.getValue().getCombatants().get(1).getNbrOfDeathChecksNeeded());
        assertEquals(HealthStatus.ALMOST2, battleCaptor.getValue().getCombatants().get(1).getHealthStatus());
        assertEquals(2, battleCaptor.getValue().getCombatants().get(1).getCurrentMove());
    }

    @Test
    void nextStep_prompt_for_death_check() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setAction(Action.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setModeName("swing");
        combatant1.setEffectiveSkillToHit(14);
        combatant1.setRollToHit(13);
        combatant1.setDamageDice(2);
        combatant1.setDamageAdds(1);
        combatant1.setRollForDamage(16);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        combatant2.setCurrentDamage(21);
        combatant2.setNbrOfDeathChecksNeeded(1);
        combatant2.setHealthStatus(HealthStatus.ALMOST);
        combatant2.setCurrentMove(2);
        ActiveDefense activeDefense = new ActiveDefense();
        activeDefense.setDefenseType(DefenseType.BLOCK);
        activeDefense.setDefendingItemName("Medium Shield");
        activeDefense.setEffectiveSkillToDefend(10);
        activeDefense.setRollToDefend(11);
        combatant2.getActiveDefenses().add(activeDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setPhase(Phase.PROMPT_FOR_DEATH_CHECK);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(Phase.PROMPT_FOR_DEATH_CHECK, null, null, null, null, null, null, null,
                null, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(Phase.RESOLVE_DEATH_CHECK, nextStep.getPhase());
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
        combatant1.setAction(Action.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setModeName("swing");
        combatant1.setEffectiveSkillToHit(14);
        combatant1.setRollToHit(13);
        combatant1.setDamageDice(2);
        combatant1.setDamageAdds(1);
        combatant1.setRollForDamage(23);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        combatant2.setCurrentDamage(31);
        combatant2.setNbrOfDeathChecksNeeded(2);
        combatant2.setHealthStatus(HealthStatus.ALMOST2);
        combatant2.setCurrentMove(2);
        ActiveDefense activeDefense = new ActiveDefense();
        activeDefense.setDefenseType(DefenseType.BLOCK);
        activeDefense.setDefendingItemName("Medium Shield");
        activeDefense.setEffectiveSkillToDefend(10);
        activeDefense.setRollToDefend(11);
        combatant2.getActiveDefenses().add(activeDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setPhase(Phase.RESOLVE_DEATH_CHECK);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(Phase.RESOLVE_DEATH_CHECK, null, null, null, null, null, null, null, null,
                null, 11);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(Phase.END, nextStep.getPhase());
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
        combatant1.setAction(Action.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setModeName("swing");
        combatant1.setEffectiveSkillToHit(14);
        combatant1.setRollToHit(13);
        combatant1.setDamageDice(2);
        combatant1.setDamageAdds(1);
        combatant1.setRollForDamage(23);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        combatant2.setCurrentDamage(31);
        combatant2.setNbrOfDeathChecksNeeded(2);
        combatant2.setHealthStatus(HealthStatus.ALMOST2);
        combatant2.setCurrentMove(2);
        ActiveDefense activeDefense = new ActiveDefense();
        activeDefense.setDefenseType(DefenseType.BLOCK);
        activeDefense.setDefendingItemName("Medium Shield");
        activeDefense.setEffectiveSkillToDefend(10);
        activeDefense.setRollToDefend(11);
        combatant2.getActiveDefenses().add(activeDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setPhase(Phase.RESOLVE_DEATH_CHECK);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(Phase.RESOLVE_DEATH_CHECK, null, null, null, null, null, null, null, null,
                null, 9);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(Phase.PROMPT_FOR_DEATH_CHECK, nextStep.getPhase());
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
        combatant1.setAction(Action.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setModeName("swing");
        combatant1.setEffectiveSkillToHit(14);
        combatant1.setRollToHit(13);
        combatant1.setDamageDice(2);
        combatant1.setDamageAdds(1);
        combatant1.setRollForDamage(23);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        combatant2.setCurrentDamage(31);
        combatant2.setNbrOfDeathChecksNeeded(1);
        combatant2.setHealthStatus(HealthStatus.ALMOST2);
        combatant2.setCurrentMove(2);
        ActiveDefense activeDefense = new ActiveDefense();
        activeDefense.setDefenseType(DefenseType.BLOCK);
        activeDefense.setDefendingItemName("Medium Shield");
        activeDefense.setEffectiveSkillToDefend(10);
        activeDefense.setRollToDefend(11);
        combatant2.getActiveDefenses().add(activeDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setPhase(Phase.RESOLVE_DEATH_CHECK);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(Phase.RESOLVE_DEATH_CHECK, null, null, null, null, null, null, null, null,
                null, 9);

        assertEquals(1, nextStep.getRound());
        assertEquals(0, nextStep.getIndex());
        assertEquals(Phase.END, nextStep.getPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNotNull(nextStep.getMessage());

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(0, battleCaptor.getValue().getCombatants().get(1).getNbrOfDeathChecksNeeded());
        assertFalse(battleCaptor.getValue().getCombatants().get(1).getDeathCheckFailed());
        assertEquals(HealthStatus.ALMOST2, battleCaptor.getValue().getCombatants().get(1).getHealthStatus());
        assertEquals(2, battleCaptor.getValue().getCombatants().get(1).getCurrentMove());
    }

    @Test
    void nextStep_end() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(testGameChar1);
        combatant1.setAction(Action.ATTACK);
        combatant1.setTargetLabel("Grunt");
        combatant1.setWeaponName("Broadsword");
        combatant1.setModeName("swing");
        combatant1.setEffectiveSkillToHit(14);
        combatant1.setRollToHit(13);
        combatant1.setDamageDice(2);
        combatant1.setDamageAdds(1);
        combatant1.setRollForDamage(23);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(testGameChar2);
        combatant2.setCurrentDamage(31);
        combatant2.setNbrOfDeathChecksNeeded(0);
        combatant2.setHealthStatus(HealthStatus.ALMOST2);
        combatant2.setCurrentMove(2);
        ActiveDefense activeDefense = new ActiveDefense();
        activeDefense.setDefenseType(DefenseType.BLOCK);
        activeDefense.setDefendingItemName("Medium Shield");
        activeDefense.setEffectiveSkillToDefend(10);
        activeDefense.setRollToDefend(11);
        combatant2.getActiveDefenses().add(activeDefense);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setPhase(Phase.END);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(Phase.END, null, null, null, null, null, null, null, null, null, null);

        assertEquals(1, nextStep.getRound());
        assertEquals(1, nextStep.getIndex());
        assertEquals(Phase.BEGIN, nextStep.getPhase());
        assertFalse(nextStep.getInputNeeded());
        assertNull(nextStep.getMessage());

        verify(battleService).updateBattle(any(Battle.class), anyString());
    }
}