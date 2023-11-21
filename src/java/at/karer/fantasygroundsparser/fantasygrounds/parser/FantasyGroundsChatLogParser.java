package at.karer.fantasygroundsparser.fantasygrounds.parser;

import at.karer.fantasygroundsparser.commandline.ErrorMessages;
import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static at.karer.fantasygroundsparser.fantasygrounds.FantasyGroundsConstants.FILE_CHATLOG;

public class FantasyGroundsChatLogParser {

    private static final Pattern HTML_TAG_FILTER = Pattern.compile("<[^>]+>+");

    public static List<ChatLogEntry> parseChatLog(Path campaignFolder) {
        var content = readFile(campaignFolder);
        var filteredChatLogs = content.stream()
                .map(FantasyGroundsChatLogParser::removeHTMLTags)
                .filter(FantasyGroundsChatLogParser::filterUnwantedChatLogEntries)
                .toList();

        var chatlogEntries = new ArrayList<ChatLogEntry>(filteredChatLogs.size() / 2);
        for (var index = 0; index < filteredChatLogs.size(); index++) {
            var chatLog = filteredChatLogs.get(index);
            ChatLogEntry entry = null;

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

            } else if (chatLog.contains("[CONCENTRATION]")) {

            } else if (chatLog.contains("Effect [")) {

            } else if (chatLog.contains("[PARTY]")) {

            }
            if (entry != null) {
                chatlogEntries.add(entry);
                index += entry.rawChatlogs() - 1;
            }
        }

        return chatlogEntries;
    }

    private static List<String> readFile(Path campaignFolder) {
        try {
            return Files.readAllLines(campaignFolder.resolve(FILE_CHATLOG));
        } catch (IOException e) {
            ErrorMessages.outputError(String.format(ErrorMessages.FILE_ACCESS_ERROR, FILE_CHATLOG));
            System.exit(1);
            return null;
        }
    }

    private static String removeHTMLTags(String rawChatLog) {
        return rawChatLog.replaceAll(HTML_TAG_FILTER.pattern(), "");
    }

    private static boolean filterUnwantedChatLogEntries(String cleanChatLog) {
        if (cleanChatLog == null || cleanChatLog.isBlank()) {
            return false;
        }
        if (cleanChatLog.contains("Attack") || cleanChatLog.contains("[ATTACK")) {
            return true;
        }
        if (cleanChatLog.contains("[Damage") || cleanChatLog.contains("[DAMAGE")) {
            return true;
        }
        if (cleanChatLog.contains("[CHECK]") || cleanChatLog.contains("[SKILL]") || cleanChatLog.contains("[INIT]")) {
            return true;
        }
        if (cleanChatLog.contains("[SAVE]") || cleanChatLog.contains("Save [")) {
            return true;
        }
        if (cleanChatLog.contains("[Heal]") || cleanChatLog.contains("[HEAL]")) {
            return true;
        }
        if (cleanChatLog.contains("[DEATH]")) {
            return true;
        }
        if (cleanChatLog.contains("Concentration [") || cleanChatLog.contains("[CONCENTRATION]")) {
            return true;
        }
        if (cleanChatLog.contains("Effect [")) {
            return true;
        }
        if (cleanChatLog.contains("[PARTY]")) {
            return true;
        }
        return false;
    }
}
