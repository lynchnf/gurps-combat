package norman.gurps.combat.model;

public enum Phase {
    //@formatter:off
    BEGIN,
    PROMPT_FOR_UNCONSCIOUSNESS_CHECK,
    RESOLVE_UNCONSCIOUSNESS_CHECK,
    PROMPT_FOR_ACTION,
    RESOLVE_ACTION,
    PROMPT_FOR_TARGET_AND_WEAPON,
    RESOLVE_TARGET_AND_WEAPON,
    PROMPT_FOR_TO_HIT,
    RESOLVE_TO_HIT,
    PROMPT_FOR_DEFENSE,
    RESOLVE_DEFENSE,
    PROMPT_FOR_TO_DEFEND,
    RESOLVE_TO_DEFEND,
    PROMPT_FOR_DAMAGE,
    RESOLVE_DAMAGE,
    PROMPT_FOR_DEATH_CHECK,
    RESOLVE_DEATH_CHECK,
    END
    //@formatter:on
}