package at.karer.fantasygroundsparser.fantasygrounds.parser;

import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry.DieType.D20;
import static at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry.DieType.STATIC;
import static at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry.Modifiers.ADVANTAGE;
import static org.assertj.core.api.Assertions.assertThat;

class AbilityCheckParserTest {
    @ParameterizedTest
    @MethodSource("provideAbilityChecks")
    void testAbilityCheckParsing(String filteredChatlog, ChatLogEntry expectedResult) {

        var result = AbilityCheckParser.createAbilityCheckEntry(filteredChatlog);

        assertThat(result).isEqualTo(expectedResult);
    }

    static Stream<Arguments> provideAbilityChecks() {
        return Stream.of(
                Arguments.of(
                        "Ashton Kr&#252;ger: [CHECK] Strength [d20+5 = 20]",
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.CHECK)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(20)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(D20, 1),
                                                new ChatLogEntry.Die(STATIC, 5)
                                        ))
                                        .modifiers(List.of())
                                        .build())
                                .rawText("Ashton Kr&#252;ger: [CHECK] Strength [d20+5 = 20]")
                                .mainActor("Ashton Kr&#252;ger")
                                .abilityName("Strength")
                                .rawChatlogs(1)
                                .build()
                ),
                Arguments.of(
                        "Derek Heath: [CHECK] Constitution [EFFECTS +1] [d20+5 = 8]",
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.CHECK)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(8)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(D20, 1),
                                                new ChatLogEntry.Die(STATIC, 5)
                                        ))
                                        .modifiers(List.of())
                                        .build())
                                .rawText("Derek Heath: [CHECK] Constitution [EFFECTS +1] [d20+5 = 8]")
                                .mainActor("Derek Heath")
                                .abilityName("Constitution")
                                .rawChatlogs(1)
                                .build()
                ),
                Arguments.of(
                        "TestCharacter Human Fighter: [SKILL] Perception [PROF] [d20+3 = 22]",
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.CHECK)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(22)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(D20, 1),
                                                new ChatLogEntry.Die(STATIC, 3)
                                        ))
                                        .modifiers(List.of())
                                        .build())
                                .rawText("TestCharacter Human Fighter: [SKILL] Perception [PROF] [d20+3 = 22]")
                                .mainActor("TestCharacter Human Fighter")
                                .abilityName("Perception")
                                .rawChatlogs(1)
                                .build()
                ),
                Arguments.of(
                        "TestCharacter Dwarven Priest: [SKILL] Persuasion [PROF] [ADV] [DROPPED 1] [g20+3 = 11]",
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.CHECK)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(11)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(D20, 1),
                                                new ChatLogEntry.Die(STATIC, 3)
                                        ))
                                        .modifiers(List.of(ADVANTAGE))
                                        .build())
                                .rawText("TestCharacter Dwarven Priest: [SKILL] Persuasion [PROF] [ADV] [DROPPED 1] [g20+3 = 11]")
                                .mainActor("TestCharacter Dwarven Priest")
                                .abilityName("Persuasion")
                                .rawChatlogs(1)
                                .build()
                ),
                Arguments.of(
                        "Ashton the Stupid: [INIT] [d20+1 = 5]",
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.CHECK)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(5)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(D20, 1),
                                                new ChatLogEntry.Die(STATIC, 1)
                                        ))
                                        .modifiers(List.of())
                                        .build())
                                .rawText("Ashton the Stupid: [INIT] [d20+1 = 5]")
                                .mainActor("Ashton the Stupid")
                                .abilityName("Initiative")
                                .rawChatlogs(1)
                                .build()
                )
        );
    }
}