package at.karer.fantasygroundsparser.fantasygrounds.model;

import lombok.Builder;

import java.util.List;
import java.util.Set;


/**
 * @param rawText The Raw text contained in the Chatlog
 * @param mainActor The actor who made this action or in case of EFFECT is affected by this action
 * @param type The type of this ChatLogEntry
 * @param diceRollResult The result of a die roll, not used for EFFECT and INVENTORY
 * @param actionResult The result of a SAVE, DEATH_SAVE, CONCENTRATION
 * @param targets The targets of an action used by ATTACK. DAMAGE, HEAL
 * @param abilityName The name of the Weapon/Spell/Skill used for this entry, used by ATTACK, DAMAGE, CHECK, SAVE, HEAL
 * @param effectType What effect was applied
 * @param rawChatlogs How many raw chatlogs where processed for this entry
 */
@Builder
public record ChatLogEntry (
    String rawText,
    String mainActor,
    ChatLogEntryType type,
    DiceRollResult diceRollResult,
    ActionResult actionResult,
    List<ActionTarget> targets,
    String abilityName,
    EffectType effectType,
    int rawChatlogs
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
        TEMP_HEAL,
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
        SAVED,
        FAILED,
        SAVE_CRITICAL,
        FAIL_CRITICAL,
        DAMAGE,
        KILLING_BLOW
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
        DEAD,
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

    public enum DieType {
        D4,
        D6,
        D8,
        D10,
        D12,
        D20,
        D100,
        STATIC,
        OTHER
    }

    /**
     * Action target is when one actor impacts another actor used for attack rolls, damage done, saving throws forced
     * @param targetName the target/receiver of the action
     * @param actionResult for attack rolls: hit/miss/critical, for saves: FAILED/SAVED, for damage: DAMAGE, KILLING_BLOW
     * @param diceRollResult the dice roll for the attack roll, saving throw or damage done
     * @param damage only filled in for damage that includes the type and how much was acutally done
     */
    @Builder
    public record ActionTarget (
        String targetName,
        ActionResult actionResult,
        DiceRollResult diceRollResult,
        Damage damage
    ) { }

    @Builder
    public record DiceRollResult (
        List<Die> dice,
        List<Modifiers> modifiers,
        int resultTotal
    ) { }

    @Builder
    public record Die (
            DieType dieType,
            int amount
    ) { }

    @Builder
    public record Damage (
        Set<DamageType> damageType,
        int damageDone,
        int overkillDamage
    ) { }
}
