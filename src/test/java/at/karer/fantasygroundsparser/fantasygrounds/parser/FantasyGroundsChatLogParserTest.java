package at.karer.fantasygroundsparser.fantasygrounds.parser;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class FantasyGroundsChatLogParserTest {

    @Test
    void testChatLogParser() {
        var testDataPath = Path.of("src/test/resources/testdata");

        var parsedChatLog = FantasyGroundsChatLogParser.parseChatLog(testDataPath);

        assertThat(parsedChatLog).hasSizeGreaterThan(0);
    }
}