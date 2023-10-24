package at.karer.fantasygroundsparser.fantasygrounds.parser;

import at.karer.fantasygroundsparser.commandline.ErrorMessages;
import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static at.karer.fantasygroundsparser.fantasygrounds.FantasyGroundsConstants.FILE_CHATLOG;

public class FantasyGroundsChatLogParser {

    private static final Pattern HTML_TAG_FILTER = Pattern.compile("<[^>]+>+");

    public static List<ChatLogEntry> parseChatLog(Path campaignFolder) {
        var content = readFile(campaignFolder);
        return content.stream()
                .map(FantasyGroundsChatLogParser::removeHTMLTags)
                .filter(FantasyGroundsChatLogParser::filterUnwantedChatLogEntries)
                .map(FantasyGroundsChatLogParser::mapToChatLog)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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
        if (cleanChatLog.contains("[Attack") || cleanChatLog.contains("[ATTACK")) {
            return true;
        }
        if (cleanChatLog.contains("[Damage") || cleanChatLog.contains("[DAMAGE")) {
            return true;
        }
        if (cleanChatLog.contains("[CHECK]") || cleanChatLog.contains("[SKILL]") || cleanChatLog.contains("[INIT]")) {
            return true;
        }
        if (cleanChatLog.contains("[SAVE]")) {
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

    private static ChatLogEntry mapToChatLog(String cleanChatLog) {
        return null;
    }
}
