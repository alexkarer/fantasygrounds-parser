package at.karer.fantasygroundsparser;

import at.karer.fantasygroundsparser.commandline.CommandLineArgs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ParserTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void testParser() {
        var args = new CommandLineArgs(Path.of("src/test/resources/testdata"));

        Parser.parse(args);

        assertThat(outContent.toString()).isNotEmpty();
        assertThat(outContent.toString()).contains("TestCharacter Human Fighter");
        assertThat(outContent.toString()).contains("TestCharacter Dwarven Priest");
    }
}