package at.karer.fantasygroundsparser.fantasygrounds.model;

import java.util.List;


/**
 * @param rawText The Raw text contained in the Chatlog
 * @param mainActor The actor who made this action or in case of EFFECT is affected by this action
 * @param type The type of this ChatLogEntry
 * @param diceRollResult The result of a dice roll, not used for EFFECT and INVENTORY
 * @param actionResult The result of a SAVE, DEATH_SAVE, CONCENTRATION
 * @param targets The targets of an action used by ATTACK. DAMAGE, HEAL
 * @param modifiers Modifiers that affect this Roll used by ATTACK, CHECK, SAVE, DEATH_SAVE
 * @param effectType What effect was applied
 */
public record ChatLogEntry (
    String rawText,
    String mainActor,
    ChatLogEntryType type,
    DiceRollResult diceRollResult,
    ActionResult actionResult,
    List<ActionTarget> targets,
    List<Modifiers> modifiers,
    EffectType effectType
) {
    /**
     * ROLL: A regular roll that is not an ability check, saving throw, attack roll
     * ATTACK: An attack roll
     * DAMAGE: Damage has been received
     * CHECK: An ability check
     * SAVE: A saving throw
     * HEAL: Healing received
     * DEATH_SAVE: Death Saving Throw
     * CONCENTRATION: Check to maintain Concentration
     * EFFECT: An effect was applied to a Person
     * INVENTORY: Item was transferred from/to the Party sheet
     */
    public enum ChatLogEntryType {
        ROLL,
        ATTACK,
        DAMAGE,
        CHECK,
        SAVE,
        HEAL,
        DEATH_SAVE,
        CONCENTRATION,
        EFFECT,
        INVENTORY
    }

    public enum ActionResult {
        HIT_CRITICAL,
        HIT,
        MISS_CRITICAL,
        MISS,
        SAVE,
        FAIL,
        SAVE_CRITICAL,
        FAIL_CRITICAL
    }

    public enum Modifiers {
        ADVANTAGE,
        DISADVANTAGE,
        PLUS2,
        PLUS5,
        MINUS2,
        MINUS5
    }

    public enum EffectType {
        PRONE,
        POISONED,
        UNCONSCIOUS
    }

    public enum DamageType {
        NO_TYPE,
        BLUDGEONING,
        PIERCING,
        SLASHING,
        FIRE,
        LIGHTNING,
        POISON,
        ACID,
        THUNDER,
        COLD,
        RADIANT,
        NECROTIC,
        PSYCHIC
    }

    public enum DiceType {
        D4,
        D6,
        D8,
        D10,
        D12,
        D20,
        D100,
        OTHER
    }

    public record ActionTarget (
        String targetName,
        ActionResult actionResult,
        DiceRollResult diceRollResult,
        DamageType damageType
    ) { }

    public record DiceRollResult (
        DiceType mainDice,
        List<DiceType> extraDice,
        int staticBonus,
        int resultTotal
    ) { }


}