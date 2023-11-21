package at.karer.fantasygroundsparser.fantasygrounds.parser;

import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry.DieType.*;
import static org.assertj.core.api.Assertions.assertThat;

public class HealingParserTest {
    @ParameterizedTest
    @MethodSource("provideHealingChatLogs")
    void testHealingParsing(List<String> filteredChatlog, ChatLogEntry expectedResult) {

        var result = HealingParser.createHealingEntry(filteredChatlog, 0);

        assertThat(result).isEqualTo(expectedResult);
    }

    static Stream<Arguments> provideHealingChatLogs() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                "TestCharacter Dwarven Priest: [HEAL] Healing Word [d4+3 = 4]"
                        ),
                        null
                ),
                Arguments.of(
                        List.of(
                                "TestCharacter Dwarven Priest: [HEAL] Healing Word [d4+3 = 4]",
                                "[Heal] [4] -&#62; [to TestCharacter Human Fighter]"
                        ),
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.HEAL)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(4)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(D4, 1),
                                                new ChatLogEntry.Die(STATIC, 3)
                                        ))
                                        .modifiers(List.of())
                                        .build())
                                .rawText("TestCharacter Dwarven Priest: [HEAL] Healing Word [d4+3 = 4]\n[Heal] [4] -&#62; [to TestCharacter Human Fighter]")
                                .mainActor("TestCharacter Dwarven Priest")
                                .targets(List.of(
                                        ChatLogEntry.ActionTarget.builder()
                                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                                        .resultTotal(4)
                                                        .dice(List.of())
                                                        .modifiers(List.of())
                                                        .build())
                                                .targetName("TestCharacter Human Fighter")
                                                .build()
                                ))
                                .abilityName("Healing Word")
                                .rawChatlogs(2)
                                .build()
                ),
                Arguments.of(
                        List.of(
                                "TestCharacter Human Fighter: [HEAL] Second Wind [d10+1 = 2]",
                                "[Heal] [2] -&#62; [to TestCharacter Human Fighter]"
                        ),
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.HEAL)
                                .rawText("TestCharacter Human Fighter: [HEAL] Second Wind [d10+1 = 2]\n[Heal] [2] -&#62; [to TestCharacter Human Fighter]")
                                .mainActor("TestCharacter Human Fighter")
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(2)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(D10, 1),
                                                new ChatLogEntry.Die(STATIC, 1)
                                        ))
                                        .modifiers(List.of())
                                        .build())
                                .targets(List.of(
                                        ChatLogEntry.ActionTarget.builder()
                                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                                        .resultTotal(2)
                                                        .dice(List.of())
                                                        .modifiers(List.of())
                                                        .build())
                                                .targetName("TestCharacter Human Fighter")
                                                .build()
                                ))
                                .abilityName("Second Wind")
                                .rawChatlogs(2)
                                .build()
                ),
                Arguments.of(
                        List.of(
                                "Rasmas: [HEAL] Heal [ = 70]",
                                "[Heal] [70] -&#62; [to Ashton Kr&#252;ger] [STATUS: Wounded]"
                        ),
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.HEAL)
                                .rawText("Rasmas: [HEAL] Heal [ = 70]\n[Heal] [70] -&#62; [to Ashton Kr&#252;ger] [STATUS: Wounded]")
                                .mainActor("Rasmas")
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(70)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(STATIC, 70)
                                        ))
                                        .modifiers(List.of())
                                        .build())
                                .targets(List.of(
                                        ChatLogEntry.ActionTarget.builder()
                                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                                        .resultTotal(70)
                                                        .dice(List.of())
                                                        .modifiers(List.of())
                                                        .build())
                                                .targetName("Ashton Kr&#252;ger")
                                                .build()
                                ))
                                .abilityName("Heal")
                                .rawChatlogs(2)
                                .build()
                ),
                Arguments.of(
                        List.of(
                                "Danny Elfman: [HEAL] Heroism [TEMP] [ = 2]",
                                "[Temporary hit points] [2] -&#62; [to Fiona d&#233; Vaun]"
                        ),
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.TEMP_HEAL)
                                .rawText("Danny Elfman: [HEAL] Heroism [TEMP] [ = 2]\n[Temporary hit points] [2] -&#62; [to Fiona d&#233; Vaun]")
                                .mainActor("Danny Elfman")
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(2)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(STATIC, 2)
                                        ))
                                        .modifiers(List.of())
                                        .build())
                                .targets(List.of(
                                        ChatLogEntry.ActionTarget.builder()
                                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                                        .resultTotal(2)
                                                        .dice(List.of())
                                                        .modifiers(List.of())
                                                        .build())
                                                .targetName("Fiona d&#233; Vaun")
                                                .build()
                                ))
                                .abilityName("Heroism")
                                .rawChatlogs(2)
                                .build()
                )
        );
    }
}