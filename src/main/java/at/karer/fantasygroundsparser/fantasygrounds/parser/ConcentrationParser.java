package at.karer.fantasygroundsparser.fantasygrounds.parser;

import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static at.karer.fantasygroundsparser.commandline.ErrorMessages.EXPECTED_TEXT_MISSING;

@Slf4j
public class ConcentrationParser {
    /**
     * Transforms raw chatlog text about a concentration roll into structured data
     * An concentration roll consists usually of 2 entries: First about the Concentration Roll,
     * the next entry includes information whether it was a success or not
     *  Concentration Roll Format: MainActor ": [CONCENTRATION] " { Modifier } " [" DiceExpression "]"
     *  Concentration Success Format: "Concentration [" Number "][vs. DC " Number "] [" MainActor "] " "[SUCCESS]"|"[FAILURE]"
     * @param filteredChatLogs all filtered chatlogs of a campaign
     * @param index current index of the chatlogs
     * @return ChatLogEntry with all information fit in for a concentration roll
     */
    static ChatLogEntry createConcentrationEntry(List<String> filteredChatLogs, int index) {
        if (index + 1 >= filteredChatLogs.size() || !filteredChatLogs.get(index + 1).contains("Concentration")) {
            return null;
        }
        var concentrationRollChatLog = filteredChatLogs.get(index);
        var concentrationSuccessChatLog = filteredChatLogs.get(index + 1);

        var builder = ChatLogEntry.builder();
        builder.type(ChatLogEntry.ChatLogEntryType.CONCENTRATION);
        builder.rawChatlogs(2);
        builder.rawText(concentrationRollChatLog + "\n" + concentrationSuccessChatLog);
        builder.mainActor(ParserUtils.getMainActorName(concentrationRollChatLog));

        var diceRollExpr = ParserUtils.getDiceRollResult(concentrationRollChatLog);
        diceRollExpr.modifiers(ParserUtils.addModifiers(concentrationRollChatLog));
        builder.diceRollResult(diceRollExpr.build());

        if (concentrationSuccessChatLog.contains("[SUCCESS]")) {
            builder.actionResult(ChatLogEntry.ActionResult.SAVED);
        } else if (concentrationSuccessChatLog.contains("[FAILURE]")) {
            builder.actionResult(ChatLogEntry.ActionResult.FAILED);
        } else {
            log.warn(EXPECTED_TEXT_MISSING, concentrationSuccessChatLog, List.of("[SUCCESS]", "[FAILURE]"));
        }

        return builder.build();
    }
}
