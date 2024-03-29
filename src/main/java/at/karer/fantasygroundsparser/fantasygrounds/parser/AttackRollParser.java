package at.karer.fantasygroundsparser.fantasygrounds.parser;

import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static at.karer.fantasygroundsparser.commandline.ErrorMessages.EXPECTED_TEXT_MISSING;

@Slf4j
public class AttackRollParser {

    private static final Set<String> ATTACK_RESULTS = Set.of("[HIT]", "[MISS]", "[CRITICAL HIT]", "[AUTOMATIC MISS]");

    /**
     * Transforms raw chatlog text about an attack roll into structured data
     * An attack consists usually of 2 or more entries: First about the Attack Roll,
     * further entries include information about the Target if it hits
     * It is possible for an attack to be made without a target => these will be ignored
     *  Attack Roll Format: AttackerName": " "[" "ATTACK" "(" "M"|"R" ")" "] " AttackName ["EFFECTS"] ["[ADV]"] ["[DIS]"] ["[DROPPED " Number "]"] "[" DiceRollResult "]"
     *  Target Hit Format: ["["] "Attack " "#" Number "(" "M"|"R" ")" ["]"] " " ["("] AttackName [")"] "[" Number "]" "[at " TargetName "] " "[HIT]"|"[MISS]"|"[AUTOMATIC MISS]"|"[CRITICAL HIT]"
     * @param filteredChatLogs all filtered chatlogs of a campaign
     * @param index current index of the chatlogs
     * @return ChatLogEntry with all information fit in for attack
     */
    static ChatLogEntry createAttackEntry(List<String> filteredChatLogs, int index) {
        var attackRollChatLog = filteredChatLogs.get(index);
        var targetHitChatLogs = new ArrayList<String>();
        int rawChatLogs = 1;

        for (int i = index + 1; i < filteredChatLogs.size() && isTargetHitChatLog(filteredChatLogs.get(i)); i++) {
            targetHitChatLogs.add(filteredChatLogs.get(i));
            rawChatLogs++;
        }
        // if next entry is not an attack this means the attack had no target => ignore
        if (targetHitChatLogs.isEmpty()) {
            return null;
        }

        var builder = ChatLogEntry.builder();

        builder.type(ChatLogEntry.ChatLogEntryType.ATTACK);
        builder.rawChatlogs(rawChatLogs);
        builder.rawText(attackRollChatLog + "\n" + String.join("\n", targetHitChatLogs));

        parseAttackRollChatLog(attackRollChatLog, builder);
        builder.targets(targetHitChatLogs.stream().map(AttackRollParser::parseTargetHitRollChatLog).toList());

        return builder.build();
    }

    private static void parseAttackRollChatLog(String attackRollChatLog, ChatLogEntry.ChatLogEntryBuilder builder) {
        builder.mainActor(ParserUtils.getMainActorName(attackRollChatLog));

        var diceRollResultBuilder = ParserUtils.getDiceRollResult(attackRollChatLog);
        var diceRollResult = diceRollResultBuilder
                .modifiers(ParserUtils.addModifiers(attackRollChatLog))
                .build();
        builder.abilityName(ParserUtils.getAbilityName(attackRollChatLog));
        builder.diceRollResult(diceRollResult);
    }

    private static ChatLogEntry.ActionTarget parseTargetHitRollChatLog(String targetHitRolLChatLog) {
        var targetNameStartIndex = targetHitRolLChatLog.indexOf("[at ") + 4;
        var targetNameEndIndex = targetHitRolLChatLog.indexOf("]", targetNameStartIndex);
        var targetName = targetHitRolLChatLog.substring(targetNameStartIndex, targetNameEndIndex).trim();

        ChatLogEntry.ActionResult actionResult = null;
        if (targetHitRolLChatLog.contains("[HIT]")) {
            actionResult = ChatLogEntry.ActionResult.HIT;
        } else if (targetHitRolLChatLog.contains("[MISS]")) {
            actionResult = ChatLogEntry.ActionResult.MISS;
        } else if (targetHitRolLChatLog.contains("[CRITICAL HIT]")) {
            actionResult = ChatLogEntry.ActionResult.HIT_CRITICAL;
        } else if (targetHitRolLChatLog.contains("[AUTOMATIC MISS]")) {
            actionResult = ChatLogEntry.ActionResult.MISS_CRITICAL;
        } else {
            log.warn(EXPECTED_TEXT_MISSING, targetHitRolLChatLog, ATTACK_RESULTS);
        }

        return ChatLogEntry.ActionTarget.builder()
                .targetName(targetName)
                .actionResult(actionResult)
                .build();
    }

    private static boolean isTargetHitChatLog(String chatLog) {
        return ATTACK_RESULTS.stream().anyMatch(chatLog::contains);
    }
}
