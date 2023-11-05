package at.karer.fantasygroundsparser.fantasygrounds.parser;

import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry.DieType.D20;
import static at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry.DieType.STATIC;
import static org.assertj.core.api.Assertions.assertThat;

class SavingThrowParserTest {

    @ParameterizedTest
    @MethodSource("provideSavingThrows")
    void testSavingThrowsParsing(List<String> filteredChatlog, ChatLogEntry expectedResult) {

        var result = SavingThrowParser.createSavingThrow(filteredChatlog, 0);

        assertThat(result).isEqualTo(expectedResult);
    }

    static Stream<Arguments> provideSavingThrows() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                "TestCharacter Human Fighter: [SAVE] Dexterity [d20+2 = 18]"
                        ),
                        null
                ),
                Arguments.of(
                        List.of(
                                "TestCharacter Human Fighter: [SAVE] Dexterity [d20+2 = 18]",
                                "Save [18][vs. DC 13] -&#62; [for TestCharacter Human Fighter] [vs Flameskull] [SUCCESS]"
                        ),
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.SAVE)
                                .abilityName("Dexterity")
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(18)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(D20, 1),
                                                new ChatLogEntry.Die(STATIC, 2)
                                        ))
                                        .modifiers(List.of())
                                        .build())
                                .rawText("TestCharacter Human Fighter: [SAVE] Dexterity [d20+2 = 18]\nSave [18][vs. DC 13] -&#62; [for TestCharacter Human Fighter] [vs Flameskull] [SUCCESS]")
                                .mainActor("Flameskull")
                                .targets(List.of(
                                        ChatLogEntry.ActionTarget.builder()
                                                .actionResult(ChatLogEntry.ActionResult.SAVED)
                                                .targetName("TestCharacter Human Fighter")
                                                .build()
                                ))
                                .rawChatlogs(2)
                                .build()
                ),
                Arguments.of(
                        List.of(
                                "Kobold 2: [SAVE] Dexterity [d20+2 = 9]",
                                "Save [9][vs. DC 13] -&#62; [for Kobold 2] [vs TestCharacter Dwarven Priest] [FAILURE]"
                        ),
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.SAVE)
                                .abilityName("Dexterity")
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(9)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(D20, 1),
                                                new ChatLogEntry.Die(STATIC, 2)
                                        ))
                                        .modifiers(List.of())
                                        .build())
                                .rawText("Kobold 2: [SAVE] Dexterity [d20+2 = 9]\nSave [9][vs. DC 13] -&#62; [for Kobold 2] [vs TestCharacter Dwarven Priest] [FAILURE]")
                                .mainActor("Flameskull")
                                .targets(List.of(
                                        ChatLogEntry.ActionTarget.builder()
                                                .actionResult(ChatLogEntry.ActionResult.FAILED)
                                                .targetName("TestCharacter Human Fighter")
                                                .build()
                                ))
                                .rawChatlogs(2)
                                .build()
                ),
                Arguments.of(
                        List.of(
                                "Ashton Kr&#252;ger: [SAVE] Dexterity [EFFECTS +1] [DIS] [DROPPED 20] [r20+2 = 17]",
                                "Save [17][vs. DC 15] -&#62; [for Ashton Kr&#252;ger] [vs Derek Heath] [SUCCESS]"
                        ),
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.SAVE)
                                .abilityName("Dexterity")
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(17)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(D20, 1),
                                                new ChatLogEntry.Die(STATIC, 2)
                                        ))
                                        .modifiers(List.of(ChatLogEntry.Modifiers.DISADVANTAGE))
                                        .build())
                                .rawText("Ashton Kr&#252;ger: [SAVE] Dexterity [EFFECTS +1] [DIS] [DROPPED 20] [r20+2 = 17]\nSave [17][vs. DC 15] -&#62; [for Ashton Kr&#252;ger] [vs Derek Heath] [SUCCESS]")
                                .mainActor("Derek Heath")
                                .targets(List.of(
                                        ChatLogEntry.ActionTarget.builder()
                                                .actionResult(ChatLogEntry.ActionResult.SAVED)
                                                .targetName("Ashton Kr&#252;ger")
                                                .build()
                                ))
                                .rawChatlogs(2)
                                .build()
                )
        );
    }
}