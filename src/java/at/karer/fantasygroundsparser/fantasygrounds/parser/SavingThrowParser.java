package at.karer.fantasygroundsparser.fantasygrounds.parser;

import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;

import java.util.List;

public class SavingThrowParser {

    /**
     * Transforms raw chatlog text about a saving throw check into structured data
     * A saving Throw consists of 1 or 2 chatlogs
     * the first one contains the roll made by the target
     * Format: TargetName ": [SAVE] " AbilityName " [" DiceExpression "]"
     * the second one contains information if the target succeeded and information about the mainActor
     * Format: "Save [" Number "][vs. DC " Number "] [for " TargetName "] [vs " MainActor "] [" "SUCCESS"|"FAILED" "]"
     * @param filteredChatLogs all filtered chatlogs of a campaign
     * @param index index pointing to a saving throw
     * @return ChatLogEntry with all information fit in for an ability check
     */
    static ChatLogEntry createSavingThrow(List<String> filteredChatLogs, int index) {
        if ((index+1) == filteredChatLogs.size() || !filteredChatLogs.get(index + 1).contains("Save [")) {
            return null;
        }
        var targetSavingThrowChatLog = filteredChatLogs.get(index);
        var savingThrowResultChatLog = filteredChatLogs.get(index+1);

        var builder = ChatLogEntry.builder();
        builder.type(ChatLogEntry.ChatLogEntryType.SAVE);
        builder.rawChatlogs(2);
        builder.rawText(targetSavingThrowChatLog + "\n" + savingThrowResultChatLog);
        builder.abilityName(ParserUtils.getAbilityName(targetSavingThrowChatLog));

        var indexMainActorStart = savingThrowResultChatLog.indexOf("[vs ");
        var indexMainActorEnd = savingThrowResultChatLog.indexOf(']', indexMainActorStart);
        builder.mainActor(savingThrowResultChatLog.substring(indexMainActorStart + 4, indexMainActorEnd).trim());

        var diceRollExpression = ParserUtils.getDiceRollExpression(targetSavingThrowChatLog);
        var diceRollResultBuilder = ParserUtils.parseDiceRollResult(diceRollExpression);
        var diceRollResult = diceRollResultBuilder
                .modifiers(ParserUtils.addModifiers(targetSavingThrowChatLog))
                .build();
        builder.diceRollResult(diceRollResult);

        var targetBuilder = ChatLogEntry.ActionTarget.builder();
        targetBuilder.targetName(ParserUtils.getMainActorName(targetSavingThrowChatLog));
        if (savingThrowResultChatLog.contains("[SUCCESS]")) {
            targetBuilder.actionResult(ChatLogEntry.ActionResult.SAVED);
        } else {
            targetBuilder.actionResult(ChatLogEntry.ActionResult.FAILED);
        }

        return builder.build();
    }
}
