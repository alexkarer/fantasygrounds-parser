package at.karer.fantasygroundsparser.fantasygrounds.parser;

import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;

import java.util.ArrayList;
import java.util.List;

public class HealingParser {
    /**
     * Transforms raw chatlog text about healing applied into structured data
     * Healing consists usually of 2 or more entries: First about the actor making the healing roll.
     * Further entries have information about how much healing was applied to a target
     * It is possible for healing to be rolled without a target => these will be ignored
     *  Healing Roll Format: MainActor ": " "[HEAL] " HealabilityName {" [EFFECTS " DiceExpression "]"} " [" DiceExpression "]"
     *  Healing applied Format: "[Heal] [" Number "] " "[to " TargetName "] " ["[STATUS: " ("Wounded"|"Heavy"|"Dying") "]"]
     * @param filteredChatLogs all filtered chatlogs of a campaign
     * @param index current index of the chatlogs containing healing entry
     * @return ChatLogEntry with all information filled in for healing applied
     */
    static ChatLogEntry createHealingEntry(List<String> filteredChatLogs, int index) {
        var healingRolledChatLog = filteredChatLogs.get(index);
        var targetHealingAppliedList = new ArrayList<String>();
        int rawChatLogs = 1;

        for (int i = index + 1;
             i < filteredChatLogs.size() && (filteredChatLogs.get(i).contains("[Heal]") || filteredChatLogs.get(i).contains("[Temporary hit points]"));
             i++) {
            targetHealingAppliedList.add(filteredChatLogs.get(i));
            rawChatLogs++;
        }
        // if next entry is not damage applied this means the damage had no target => ignore
        if (targetHealingAppliedList.isEmpty()) {
            return null;
        }

        var builder = ChatLogEntry.builder();

        if (healingRolledChatLog.contains("[TEMP]")) {
            builder.type(ChatLogEntry.ChatLogEntryType.TEMP_HEAL);
        } else {
            builder.type(ChatLogEntry.ChatLogEntryType.HEAL);
        }
        builder.rawChatlogs(rawChatLogs);
        builder.rawText(healingRolledChatLog + "\n" + String.join("\n", targetHealingAppliedList));

        builder.mainActor(ParserUtils.getMainActorName(healingRolledChatLog));
        builder.abilityName(ParserUtils.getAbilityName(healingRolledChatLog));
        builder.diceRollResult(ParserUtils.getDiceRollResult(healingRolledChatLog).modifiers(List.of()).build());

        builder.targets(targetHealingAppliedList.stream()
                .map(HealingParser::parseHealingAppliedChatLog)
                .toList());

        return builder.build();
    }

    private static ChatLogEntry.ActionTarget parseHealingAppliedChatLog(String targetHealingAppliedChatLog) {
        var targetNameStartIndex = targetHealingAppliedChatLog.indexOf("[to ") + 4;
        var targetNameEndIndex = targetHealingAppliedChatLog.indexOf("]", targetNameStartIndex);
        var targetName = targetHealingAppliedChatLog.substring(targetNameStartIndex, targetNameEndIndex).trim();

        var healingDoneStartIndex = targetHealingAppliedChatLog.indexOf("[", 6) + 1;
        var healingDoneEndIndex = targetHealingAppliedChatLog.indexOf("]", healingDoneStartIndex);
        var healingDoneString = targetHealingAppliedChatLog.substring(healingDoneStartIndex, healingDoneEndIndex).trim();
        var healingDone = Integer.parseInt(healingDoneString);

        return ChatLogEntry.ActionTarget.builder()
                .targetName(targetName)
                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                        .resultTotal(healingDone)
                        .dice(List.of())
                        .modifiers(List.of())
                        .build())
                .build();
    }
}
