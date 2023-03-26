# GURPS Combat Dashboard

A play aid for Gamemasters running a [GURPS](http://www.sjgames.com/gurps/) game.

### Currently working on: Fix integration tests.

* Handle natural DR, add name to ArmorPiece and change HitLocation to a list, handle shock penalty, and add defence
  bonus to combatant.

## TODO

* Handle weapon ready and shield ready.
* Handle critical results.
* Allow edit of combatants in the middle of combatant.
* Handle Ranged weapons.
* Allow attack to targeted hit hitLocation.
* Allow attack to randomly chosen hit hitLocation.
* Handle flexible and blunt force trauma.
* Handle mortal injury.
* Handle stun and knockdown.
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

* Create class CombatantAttack and move attack related properties to it.
* Move all text messages and logs to a message file.
