package at.karer.fantasygroundsparser.fantasygrounds.parser;

import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry.DieType.D20;
import static org.assertj.core.api.Assertions.assertThat;

class DeathSavingThrowParserTest {
    @ParameterizedTest
    @MethodSource("provideDeathSave")
    void testDeathSaveParsing(String filteredChatlog, ChatLogEntry expectedResult) {

        var result = DeathSavingThrowParser.createDeathSavingThrowEntry(filteredChatlog);

        assertThat(result).isEqualTo(expectedResult);
    }

    static Stream<Arguments> provideDeathSave() {
        return Stream.of(
                Arguments.of(
                        "Derek Heath: [DEATH] [CRITICAL SUCCESS] [d20 = 20]",
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.DEATH_SAVE)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(20)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(D20, 1)
                                        ))
                                        .modifiers(List.of())
                                        .build())
                                .rawText("Derek Heath: [DEATH] [CRITICAL SUCCESS] [d20 = 20]")
                                .mainActor("Derek Heath")
                                .actionResult(ChatLogEntry.ActionResult.SAVE_CRITICAL)
                                .rawChatlogs(1)
                                .build()
                ),
                Arguments.of(
                        "TestCharacter Dwarven Priest: [DEATH] [SUCCESS] [d20 = 10]",
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.DEATH_SAVE)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(10)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(D20, 1)
                                        ))
                                        .modifiers(List.of())
                                        .build())
                                .rawText("TestCharacter Dwarven Priest: [DEATH] [SUCCESS] [d20 = 10]")
                                .mainActor("TestCharacter Dwarven Priest")
                                .actionResult(ChatLogEntry.ActionResult.SAVED)
                                .rawChatlogs(1)
                                .build()
                ),
                Arguments.of(
                        "TestCharacter Dwarven Priest: [DEATH] [FAILURE][d20 = 7]",
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.DEATH_SAVE)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(7)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(D20, 1)
                                        ))
                                        .modifiers(List.of())
                                        .build())
                                .rawText("TestCharacter Dwarven Priest: [DEATH] [FAILURE][d20 = 7]")
                                .mainActor("TestCharacter Dwarven Priest")
                                .actionResult(ChatLogEntry.ActionResult.FAILED)
                                .rawChatlogs(1)
                                .build()
                ),
                Arguments.of(
                        "TestCharacter Dwarven Priest: [DEATH] [FAILURE][STATUS: Dead] [d20 = 4]",
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.DEATH_SAVE)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(4)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(D20, 1)
                                        ))
                                        .modifiers(List.of())
                                        .build())
                                .rawText("TestCharacter Dwarven Priest: [DEATH] [FAILURE][STATUS: Dead] [d20 = 4]")
                                .mainActor("TestCharacter Dwarven Priest")
                                .actionResult(ChatLogEntry.ActionResult.FAILED)
                                .effectType(ChatLogEntry.EffectType.DEAD)
                                .rawChatlogs(1)
                                .build()
                ),
                Arguments.of(
                        "Ashton Kr&#252;ger: [DEATH] [CRITICAL FAILURE] [d20 = 1]",
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.DEATH_SAVE)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(1)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(D20, 1)
                                        ))
                                        .modifiers(List.of())
                                        .build())
                                .rawText("Ashton Kr&#252;ger: [DEATH] [CRITICAL FAILURE] [d20 = 1]")
                                .mainActor("Ashton Kr&#252;ger")
                                .actionResult(ChatLogEntry.ActionResult.FAIL_CRITICAL)
                                .rawChatlogs(1)
                                .build()
                )
        );
    }
}
