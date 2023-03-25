# GURPS Combat Dashboard

A play aid for Gamemasters running a [GURPS](http://www.sjgames.com/gurps/) game.

## TODO

* Handle weapon ready and shield ready.
* Handle critical results.
* Allow edit of combatants in the middle of combatant.
* Handle Ranged weapons.
* Allow attack to targeted hit hitLocation.
* Allow attack to randomly chosen hit hitLocation.
* Handle natural DR.
* Allow other actions:
    * Aim a Ranged Attack
    * All-Out Attack: +1 to hit for ranged attack
    * All-Out Attack: +4 to hit for melee attack
    * All-Out Attack: +2 to damage for melee attack
    * All-Out Attack: two melee attacks.
    * All-Out Defense: +2 to a valid defenseType.
    * All-Out Defense: two valid defenses.
    * Change Posture
    * Concentrate
    * Do Nothing
    * Move
    * Move and Ranged Attack
    * Move and Melee Attack
    * Ready a Weapon
    * Wait

## Hardening Ideas

* Rename Action to ActionType.
* Rename Location to HitLocation.
* Rename SkillRollResult to ResultType.
* Rename Phase to CombatPhase.
* Rename ActiveDefense to CombatantDefense.
* Rename Armor to ArmorPiece.
*
* Create class CombatantAttack and move attack related properties to it.
* Add name to ArmorPiece and change hitLocation to a list.
* Move all text messages and logs to a message file.
