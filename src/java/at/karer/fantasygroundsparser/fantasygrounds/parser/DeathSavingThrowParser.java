package at.karer.fantasygroundsparser.fantasygrounds.parser;

import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;

public class DeathSavingThrowParser {

    /**
     * Transforms raw chatlog text about a death saving throw into structured data
     * A Death Saving Throw consists of only 1 Entry
     * Death Saving Throw: Name ": [DEATH] " "[CRITICAL SUCCESS]"|"[SUCCESS]"|"[FAILURE]"|"[CRITICAL FAILURE]" {Effects ... (Adv/+1d4, ...)} ["[STATUS: Dead]"] " [" DiceExpression "]"
     * @param deathSaveChatLog the raw chatlog information about the death save
     * @return ChatLogEntry with all information fit in for a death save
     */
    static ChatLogEntry createDeathSavingThrowEntry(String deathSaveChatLog) {
        var builder = ChatLogEntry.builder();
        builder.type(ChatLogEntry.ChatLogEntryType.DEATH_SAVE);
        builder.rawChatlogs(1);
        builder.rawText(deathSaveChatLog);
        builder.mainActor(ParserUtils.getMainActorName(deathSaveChatLog));

        if (deathSaveChatLog.contains("[CRITICAL SUCCESS]")) {
            builder.actionResult(ChatLogEntry.ActionResult.SAVE_CRITICAL);
        } else if (deathSaveChatLog.contains("[SUCCESS]")) {
            builder.actionResult(ChatLogEntry.ActionResult.SAVED);
        } else if (deathSaveChatLog.contains("[CRITICAL FAILURE]")) {
            builder.actionResult(ChatLogEntry.ActionResult.FAIL_CRITICAL);
        } else if (deathSaveChatLog.contains("[FAILURE]")) {
            builder.actionResult(ChatLogEntry.ActionResult.FAILED);
        }

        if (deathSaveChatLog.contains("[STATUS: Dead]")) {
            builder.effectType(ChatLogEntry.EffectType.DEAD);
        }

        var diceRollResultBuilder = ParserUtils.getDiceRollResult(deathSaveChatLog);
        var diceRollResult = diceRollResultBuilder
                .modifiers(ParserUtils.addModifiers(deathSaveChatLog))
                .build();
        builder.diceRollResult(diceRollResult);

        return builder.build();
    }
}
