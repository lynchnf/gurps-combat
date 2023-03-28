package norman.gurps.combat.service;

import norman.gurps.combat.TestHelper;
import norman.gurps.combat.model.Battle;
import norman.gurps.combat.model.CombatPhase;
import norman.gurps.combat.model.Combatant;
import norman.gurps.combat.model.GameChar;
import norman.gurps.combat.model.NextStep;
import norman.gurps.combat.service.combat.CombatActionComponent;
import norman.gurps.combat.service.combat.CombatAimTargetComponent;
import norman.gurps.combat.service.combat.CombatBeginTurnComponent;
import norman.gurps.combat.service.combat.CombatDeathCheckComponent;
import norman.gurps.combat.service.combat.CombatDefenseComponent;
import norman.gurps.combat.service.combat.CombatEndTurnComponent;
import norman.gurps.combat.service.combat.CombatForDamageComponent;
import norman.gurps.combat.service.combat.CombatMeleeTargetComponent;
import norman.gurps.combat.service.combat.CombatRangedTargetComponent;
import norman.gurps.combat.service.combat.CombatToDefendComponent;
import norman.gurps.combat.service.combat.CombatToHitComponent;
import norman.gurps.combat.service.combat.CombatUnconsciousnessCheckComponent;
import norman.gurps.combat.service.combat.CombatUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CombatServiceTest {
    CombatService service;
    BattleService battleService;
    CombatBeginTurnComponent beginTurn;
    CombatUnconsciousnessCheckComponent unconsciousnessCheck;
    CombatActionComponent action;
    CombatMeleeTargetComponent meleeTarget;
    CombatAimTargetComponent aimTarget;
    CombatRangedTargetComponent rangedTarget;
    CombatToHitComponent toHit;
    CombatDefenseComponent defense;
    CombatToDefendComponent toDefend;
    CombatForDamageComponent forDamage;
    CombatDeathCheckComponent deathCheck;
    CombatEndTurnComponent endTurn;
    CombatUtils utils;
    GameChar gameChar1;
    GameChar gameChar2;
    ArgumentCaptor<Battle> battleCaptor;

    @BeforeEach
    void setUp() {
        battleService = mock(BattleService.class);
        beginTurn = mock(CombatBeginTurnComponent.class);
        unconsciousnessCheck = mock(CombatUnconsciousnessCheckComponent.class);
        action = mock(CombatActionComponent.class);
        meleeTarget = mock(CombatMeleeTargetComponent.class);
        aimTarget = mock(CombatAimTargetComponent.class);
        rangedTarget = mock(CombatRangedTargetComponent.class);
        toHit = mock(CombatToHitComponent.class);
        defense = mock(CombatDefenseComponent.class);
        toDefend = mock(CombatToDefendComponent.class);
        forDamage = mock(CombatForDamageComponent.class);
        deathCheck = mock(CombatDeathCheckComponent.class);
        endTurn = mock(CombatEndTurnComponent.class);
        utils = mock(CombatUtils.class);
        service = new CombatService(battleService, beginTurn, unconsciousnessCheck, action, meleeTarget, aimTarget,
                rangedTarget, toHit, defense, toDefend, forDamage, deathCheck, endTurn, utils);
        gameChar1 = TestHelper.getGameChar1();
        gameChar2 = TestHelper.getGameChar2();
        battleCaptor = ArgumentCaptor.forClass(Battle.class);
    }

    @Test
    void startCombat() {
        Battle battle = new Battle();
        HashSet<String> existingLabels = new HashSet<>();
        Combatant combatant1 = new Combatant(gameChar2, existingLabels);
        existingLabels.add(combatant1.getLabel());
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = new Combatant(gameChar1, existingLabels);
        existingLabels.add(combatant2.getLabel());
        battle.getCombatants().add(combatant2);
        GameChar gameChar3 = TestHelper.getGameChar2();
        gameChar3.setDexterity(11);
        Combatant combatant3 = new Combatant(gameChar3, existingLabels);
        battle.getCombatants().add(combatant3);
        when(battleService.getBattle()).thenReturn(battle);

        service.startCombat();

        verify(battleService).updateBattle(battleCaptor.capture(), anyString());
        assertEquals(3, battleCaptor.getValue().getCombatants().size());
        assertEquals("Bob the Example", battleCaptor.getValue().getCombatants().get(0).getLabel());
        assertEquals("Grunt 2", battleCaptor.getValue().getCombatants().get(1).getLabel());
        assertEquals("Grunt", battleCaptor.getValue().getCombatants().get(2).getLabel());
        assertEquals(1, battleCaptor.getValue().getNextStep().getRound());
        assertEquals(0, battleCaptor.getValue().getNextStep().getIndex());
        assertEquals(CombatPhase.BEGIN_TURN, battleCaptor.getValue().getNextStep().getCombatPhase());
    }

    @Test
    void nextStep_begin() {
        Battle battle = new Battle();
        Combatant combatant1 = TestHelper.getCombatant(gameChar1);
        battle.getCombatants().add(combatant1);
        Combatant combatant2 = TestHelper.getCombatant(gameChar2);
        battle.getCombatants().add(combatant2);
        NextStep nextStep1 = new NextStep();
        nextStep1.setRound(1);
        nextStep1.setIndex(0);
        nextStep1.setCombatPhase(CombatPhase.BEGIN_TURN);
        battle.setNextStep(nextStep1);
        when(battleService.getBattle()).thenReturn(battle);

        NextStep nextStep = service.nextStep(CombatPhase.BEGIN_TURN, null, null, null, null, null, null, null, null,
                null, null, null, null);

        InOrder inOrder = inOrder(beginTurn);
        inOrder.verify(beginTurn).updateAttacker(combatant1);
        inOrder.verify(beginTurn).resolve(1, 0, combatant1);
        verify(battleService).updateBattle(any(Battle.class), anyString());
    }
}
