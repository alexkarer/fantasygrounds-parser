package at.karer.fantasygroundsparser.fantasygrounds.parser;

import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FantasyGroundsChatLogParserTest {

    @Test
    void testChatLogParser() {
        var testDataPath = Path.of("src/test/resources/testdata");

        var parsedChatLog = FantasyGroundsChatLogParser.parseChatLog(testDataPath);

        assertThat(parsedChatLog).hasSizeGreaterThan(0);
        containsChatLogEntryType(parsedChatLog, ChatLogEntry.ChatLogEntryType.ATTACK);
        containsChatLogEntryType(parsedChatLog, ChatLogEntry.ChatLogEntryType.DAMAGE);
        containsChatLogEntryType(parsedChatLog, ChatLogEntry.ChatLogEntryType.SAVE);
        containsChatLogEntryType(parsedChatLog, ChatLogEntry.ChatLogEntryType.CHECK);
        containsChatLogEntryType(parsedChatLog, ChatLogEntry.ChatLogEntryType.HEAL);
    }

    private void containsChatLogEntryType(List<ChatLogEntry> chatLogs, ChatLogEntry.ChatLogEntryType type) {
        assertThat(chatLogs.stream()
                .filter(chatLogEntry -> type == chatLogEntry.type())
                .findAny()
        ).isPresent();
    }
}