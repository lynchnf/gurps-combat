# GURPS Combat Dashboard

A play aid for Gamemasters running a [GURPS](http://www.sjgames.com/gurps/) game.

## Currently working on

* Change weaponModeName to modeName.
* Combine weaponName and defendingItemName.

## TODO

* Combine toHitRoll, toDefendRoll, forDeathCheckRoll, and forUnconsciousnessCheckRoll.
* Validate defenses for ranged weapons.
* Clear combatRanged if not aiming or attacking with a ranged weapon.
* Preferences
* Handle weapons that require multiple ready turns.
* Handle fast draw rolls.
* Allow edit of combatants in the middle of combatant.
* Allow attack to targeted hit hitLocation.
* Allow attack to randomly chosen hit hitLocation.
* Handle flexible and blunt force trauma.
* Handle mortal injury.
* Handle stun and knockdown.
* Handle undo and rollback of combat actions.
* Clear combatMelee if not evaluating or attacking with a melee weapon.
* Zero out aim/evaluate bonuses for move & attack.
* Handle weapons with multiple ready rounds.
* Handle ranged weapons with a rate of fire greater than 1.
* Handle free actions.
* Handle partial cover.
* Allow other actions:
    * Move
    * Change Posture
    * Evaluate
    * All-Out Attack: two melee attacks
    * All-Out Attack: feint and melee attack
    * All-Out Attack: suppression fire
    * All-Out Defense: two valid defenses
    * Concentrate
    * Move
    * Ready a Weapon
    * Wait

## Hardening Ideas

* Move all text messages and logs to a message file.
* Create new exception for validation errors ... or something.
* Go to an event driven architecture ... whatever that means.
