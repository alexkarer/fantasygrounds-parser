package at.karer.fantasygroundsparser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

class MainTest {

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
    void testMain() {
        Main.main(new String[]{"-campaign", "src/test/resources/testdata"});

        assertThat(outContent.toString()).isNotEmpty();
        assertThat(outContent.toString()).contains("TestCharacter Human Fighter");
        assertThat(outContent.toString()).contains("TestCharacter Dwarven Priest");
    }
}