package at.karer.fantasygroundsparser.fantasygrounds.parser;

import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;

import java.util.List;

public class AttackRollParser {

    /**
     * Transforms a chatlog about an attack roll
     * An attack consists usually of 2 entries: First about the Attack Roll, Second information about the Target if it hits
     * It is possible for an attack to be made without a target => these will be ignored
     * It is also possible for attacks to have multiple targets, this is rare and will maybe be added later
     *  Attack Roll Format: AttackerName": " "[" "ATTACK" "(" "M"|"R" ")" "] " AttackName ["EFFECTS"] ["[ADV]"] ["[DIS]"] ["[DROPPED " Number "]"] "[" DiceRollResult "]"
     *  Target Hit Format: ["["] "Attack " "#" Number "(" "M"|"R" ")" ["]"] " " ["("] AttackName [")"] "[" Number "]" "[at " TargetName "] " "[HIT]"|"[MISS]"|"[AUTOMATIC MISS]"|"[CRITICAL HIT]"
     * @param filteredChatLogs all filtered chatlogs of a campaign
     * @param index current index of the chatlogs
     * @return ChatLogEntry with all information fit in for attack
     */
    static ChatLogEntry createAttackEntry(List<String> filteredChatLogs, int index) {
        // if next entry is not an attack this means the attack had no target => ignore
        if (index == (filteredChatLogs.size() - 1) || !filteredChatLogs.get(index + 1).contains("Attack")) {
            return null;
        }
        var attackRollChatLog = filteredChatLogs.get(index);
        var targetHitChatLog = filteredChatLogs.get(index + 1);
        var builder = ChatLogEntry.builder();

        builder.type(ChatLogEntry.ChatLogEntryType.ATTACK);
        builder.rawText(attackRollChatLog + "\n" + targetHitChatLog);

        parseAttackRollChatLog(attackRollChatLog, builder);
        builder.targets(List.of(parseTargetHitRollChatLog(targetHitChatLog)));

        return builder.build();
    }

    private static void parseAttackRollChatLog(String attackRollChatLog, ChatLogEntry.ChatLogEntryBuilder builder) {
        var indexAttacker = attackRollChatLog.indexOf(":");
        builder.mainActor(attackRollChatLog.substring(0, indexAttacker));

        var indexDiceRollResult = attackRollChatLog.lastIndexOf("[");
        var diceRollExpression = attackRollChatLog.substring(indexDiceRollResult + 1, attackRollChatLog.length() - 1);
        var diceRollResultBuilder = ParserUtils.parseDiceRollResult(diceRollExpression);
        var diceRollResult = diceRollResultBuilder
                .modifiers(ParserUtils.addModifiers(attackRollChatLog))
                .build();
        builder.diceRollResult(diceRollResult);
    }

    private static ChatLogEntry.ActionTarget parseTargetHitRollChatLog(String targetHitRolLChatLog) {
        var indexTargetStart = targetHitRolLChatLog.indexOf("[at ") + 4;
        var targetNameTemp = targetHitRolLChatLog.substring(indexTargetStart);
        var targetName = targetNameTemp.substring(0, targetNameTemp.indexOf("]"));

        ChatLogEntry.ActionResult actionResult = null;
        if (targetHitRolLChatLog.contains("[HIT]")) {
            actionResult = ChatLogEntry.ActionResult.HIT;
        } else if (targetHitRolLChatLog.contains("[MISS]")) {
            actionResult = ChatLogEntry.ActionResult.MISS;
        } else if (targetHitRolLChatLog.contains("[CRITICAL HIT]")) {
            actionResult = ChatLogEntry.ActionResult.HIT_CRITICAL;
        } else if (targetHitRolLChatLog.contains("[AUTOMATIC MISS]")) {
            actionResult = ChatLogEntry.ActionResult.MISS_CRITICAL;
        }

        return ChatLogEntry.ActionTarget.builder()
                .targetName(targetName)
                .actionResult(actionResult)
                .build();
    }
}
