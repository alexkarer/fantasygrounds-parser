package at.karer.fantasygroundsparser.fantasygrounds.parser;

import at.karer.fantasygroundsparser.commandline.ErrorMessages;
import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static at.karer.fantasygroundsparser.commandline.ErrorMessages.GENERAL_PARSING_ERROR;
import static at.karer.fantasygroundsparser.fantasygrounds.FantasyGroundsConstants.FILE_CHATLOG;

@Slf4j
public class FantasyGroundsChatLogParser {
    private static final Pattern HTML_TAG_FILTER = Pattern.compile("<[^>]+>+");

    public static List<ChatLogEntry> parseChatLog(Path campaignFolder) {
        var content = readFile(campaignFolder);
        var filteredChatLogs = content.stream()
                .map(FantasyGroundsChatLogParser::removeHTMLTags)
                .toList();

        var chatlogEntries = new ArrayList<ChatLogEntry>(filteredChatLogs.size() / 2);
        for (var index = 0; index < filteredChatLogs.size(); index++) {
            var chatLog = filteredChatLogs.get(index);
            ChatLogEntry entry = null;

            try {
                if (chatLog.contains("[ATTACK")) {
                    entry = AttackRollParser.createAttackEntry(filteredChatLogs, index);
                } else if (chatLog.contains("[DAMAGE")) {
                    entry = DamageParser.createDamageEntry(filteredChatLogs, index);
                } else if (chatLog.contains("[CHECK]") || chatLog.contains("[SKILL]") || chatLog.contains("[INIT]")) {
                    entry = AbilityCheckParser.createAbilityCheckEntry(filteredChatLogs.get(index));
                } else if (chatLog.contains("[SAVE]")) {
                    entry = SavingThrowParser.createSavingThrow(filteredChatLogs, index);
                } else if (chatLog.contains("[HEAL]")) {
                    entry = HealingParser.createHealingEntry(filteredChatLogs, index);
                } else if (chatLog.contains("[DEATH]")) {
                    entry = DeathSavingThrowParser.createDeathSavingThrowEntry(filteredChatLogs.get(index));
                } else if (chatLog.contains("[CONCENTRATION]")) {
                    entry = ConcentrationParser.createConcentrationEntry(filteredChatLogs, index);
                } else if (chatLog.contains("Effect [")) {

                } else if (chatLog.contains("[PARTY]")) {

                }
            } catch (Exception e) {
                log.warn(GENERAL_PARSING_ERROR, index, chatLog, e.getMessage(), e);
            }
            if (entry != null) {
                chatlogEntries.add(entry);
                index += entry.rawChatlogs() - 1;
            }
        }

        log.info("Finished parsing {}, generated {} chatlogEntries", FILE_CHATLOG, chatlogEntries.size());

        return chatlogEntries;
    }

    private static List<String> readFile(Path campaignFolder) {
        try {
            return Files.readAllLines(campaignFolder.resolve(FILE_CHATLOG));
        } catch (IOException e) {
            log.error(ErrorMessages.FILE_ACCESS_ERROR, FILE_CHATLOG);
            System.exit(1);
            return null;
        }
    }

    private static String removeHTMLTags(String rawChatLog) {
        return rawChatLog.replaceAll(HTML_TAG_FILTER.pattern(), "");
    }
}
