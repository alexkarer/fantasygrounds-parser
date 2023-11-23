package at.karer.fantasygroundsparser.fantasygrounds.parser;

import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry.DieType.*;
import static org.assertj.core.api.Assertions.assertThat;

class ConcentrationParserTest {
    @ParameterizedTest
    @MethodSource("provideConcentrationRollChatlogs")
    void testAttackRollParsing(List<String> filteredChatlogs, ChatLogEntry expectedResult) {

        var result = ConcentrationParser.createConcentrationEntry(filteredChatlogs, 0);

        assertThat(result).isEqualTo(expectedResult);
    }

    static Stream<Arguments> provideConcentrationRollChatlogs() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                "Derek Heath: [CONCENTRATION] [EFFECTS -1d4] [d20-p4+4 = 7]"
                        ),
                        null
                ),
                Arguments.of(
                        List.of(
                                "Derek Heath: [CONCENTRATION] [EFFECTS -1d4] [d20-p4+4 = 7]",
                                "Concentration [7][vs. DC 10] -&#62; [for Derek Heath] [FAILURE]"
                        ),
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.CONCENTRATION)
                                .actionResult(ChatLogEntry.ActionResult.FAILED)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(7)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(D20, 1),
                                                new ChatLogEntry.Die(D4, 1),
                                                new ChatLogEntry.Die(STATIC, 4)
                                        ))
                                        .modifiers(List.of())
                                        .build())
                                .rawText("Derek Heath: [CONCENTRATION] [EFFECTS -1d4] [d20-p4+4 = 7]\nConcentration [7][vs. DC 10] -&#62; [for Derek Heath] [FAILURE]")
                                .mainActor("Derek Heath")
                                .rawChatlogs(2)
                                .build()
                ),
                Arguments.of(
                        List.of(
                                "Rasmas: [CONCENTRATION] [WAR CASTER] [ADV] [DROPPED 1] [g20+3 = 22]",
                                "Concentration [22][vs. DC 10] -&#62; [for Rasmas] [SUCCESS]"
                        ),
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.CONCENTRATION)
                                .actionResult(ChatLogEntry.ActionResult.SAVED)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(22)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(D20, 1),
                                                new ChatLogEntry.Die(STATIC, 3)
                                        ))
                                        .modifiers(List.of(ChatLogEntry.Modifiers.ADVANTAGE))
                                        .build())
                                .rawText("Rasmas: [CONCENTRATION] [WAR CASTER] [ADV] [DROPPED 1] [g20+3 = 22]\nConcentration [22][vs. DC 10] -&#62; [for Rasmas] [SUCCESS]")
                                .mainActor("Rasmas")
                                .rawChatlogs(2)
                                .build()
                ),
                Arguments.of(
                        List.of(
                                "Ashton Kr&#252;ger: [CONCENTRATION][d20+6 = 21]",
                                "Concentration [21][vs. DC 10] -&#62; [for Ashton Kr&#252;ger] [SUCCESS]"
                        ),
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.CONCENTRATION)
                                .actionResult(ChatLogEntry.ActionResult.SAVED)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(21)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(D20, 1),
                                                new ChatLogEntry.Die(STATIC, 6)
                                        ))
                                        .modifiers(List.of())
                                        .build())
                                .rawText("Ashton Kr&#252;ger: [CONCENTRATION][d20+6 = 21]\nConcentration [21][vs. DC 10] -&#62; [for Ashton Kr&#252;ger] [SUCCESS]")
                                .mainActor("Ashton Kr&#252;ger")
                                .rawChatlogs(2)
                                .build()
                )
        );
    }
}