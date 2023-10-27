package at.karer.fantasygroundsparser.fantasygrounds.parser;

import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;
import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry.Die;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry.DieType.*;
import static at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry.Modifiers.*;
import static org.assertj.core.api.Assertions.assertThat;

class AttackRollParserTest {

    @ParameterizedTest
    @MethodSource("provideAttackRolls")
    void testAttackRollParsing(List<String> filteredChatlog, ChatLogEntry expectedResult) {

        var result = AttackRollParser.createAttackEntry(filteredChatlog, 0);

        assertThat(result).isEqualTo(expectedResult);
    }

    static Stream<Arguments> provideAttackRolls() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                "TestCharacter Human Fighter: [ATTACK (M)] Greatsword [d20+5 = 18]"
                        ),
                        null
                ),
                Arguments.of(
                        List.of(
                                "TestCharacter Human Fighter: [ATTACK (M)] Greatsword [d20+5 = 18]",
                                "Attack (M) (Greatsword) [18] -&#62; [at Bugbear] [HIT]"
                        ),
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.ATTACK)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(18)
                                        .dice(List.of(
                                                new Die(D20, 1),
                                                new Die(STATIC, 5)
                                        ))
                                        .modifiers(List.of())
                                        .build())
                                .rawText("TestCharacter Human Fighter: [ATTACK (M)] Greatsword [d20+5 = 18]\nAttack (M) (Greatsword) [18] -&#62; [at Bugbear] [HIT]")
                                .mainActor("TestCharacter Human Fighter")
                                .targets(List.of(
                                        ChatLogEntry.ActionTarget.builder()
                                                .actionResult(ChatLogEntry.ActionResult.HIT)
                                                .targetName("Bugbear")
                                                .build()
                                ))
                                .build()
                ),
                Arguments.of(
                        List.of(
                                "Duergar: [ATTACK (M)] War pick [EFFECTS] [ADV] [DROPPED 12] [g20+4 = 23]",
                                "Attack [23] -&#62; [at Jack Heart] [HIT]"
                        ),
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.ATTACK)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(23)
                                        .dice(List.of(
                                                new Die(D20, 1),
                                                new Die(STATIC, 4)
                                        ))
                                        .modifiers(List.of(ADVANTAGE))
                                        .build())
                                .rawText("Duergar: [ATTACK (M)] War pick [EFFECTS] [ADV] [DROPPED 12] [g20+4 = 23]\nAttack [23] -&#62; [at Jack Heart] [HIT]")
                                .mainActor("Duergar")
                                .targets(List.of(
                                        ChatLogEntry.ActionTarget.builder()
                                                .actionResult(ChatLogEntry.ActionResult.HIT)
                                                .targetName("Jack Heart")
                                                .build()
                                ))
                                .build()
                ),
                Arguments.of(
                        List.of(
                                "Srenga Tempest: [ATTACK (M)] Shortsword [+2] [d20+7 = 8]",
                                "Attack [8] -&#62; [at Duergar] [AUTOMATIC MISS]"
                        ),
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.ATTACK)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(8)
                                        .dice(List.of(
                                                new Die(D20, 1),
                                                new Die(STATIC, 7)
                                        ))
                                        .modifiers(List.of(PLUS2))
                                        .build())
                                .rawText("Srenga Tempest: [ATTACK (M)] Shortsword [+2] [d20+7 = 8]\nAttack [8] -&#62; [at Duergar] [AUTOMATIC MISS]")
                                .mainActor("Srenga Tempest")
                                .targets(List.of(
                                        ChatLogEntry.ActionTarget.builder()
                                                .actionResult(ChatLogEntry.ActionResult.MISS_CRITICAL)
                                                .targetName("Duergar")
                                                .build()
                                ))
                                .build()
                ),
                Arguments.of(
                        List.of(
                                "Derek Heath's Eldritch Cannon: [ATTACK (R)] Force Ballista [ADV] [DIS] [d20+4 = 9]",
                                "[Attack (R)] Force Ballista [9] -&#62; [at Warhorse Skeleton] [MISS]"
                        ),
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.ATTACK)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(9)
                                        .dice(List.of(
                                                new Die(D20, 1),
                                                new Die(STATIC, 4)
                                        ))
                                        .modifiers(List.of(ADVANTAGE, DISADVANTAGE))
                                        .build())
                                .rawText("Derek Heath's Eldritch Cannon: [ATTACK (R)] Force Ballista [ADV] [DIS] [d20+4 = 9]\n[Attack (R)] Force Ballista [9] -&#62; [at Warhorse Skeleton] [MISS]")
                                .mainActor("Derek Heath's Eldritch Cannon")
                                .targets(List.of(
                                        ChatLogEntry.ActionTarget.builder()
                                                .actionResult(ChatLogEntry.ActionResult.MISS)
                                                .targetName("Warhorse Skeleton")
                                                .build()
                                ))
                                .build()
                ),
                Arguments.of(
                        List.of(
                                "Derek Heath: [ATTACK (R)] Fire Bolt [ADV] [DROPPED 4] [g20+4 = 12]",
                                "[Attack #1 (R)] Fire Bolt [12] -&#62; [at Warhorse Skeleton] [MISS]"
                        ),
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.ATTACK)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(12)
                                        .dice(List.of(
                                                new Die(D20, 1),
                                                new Die(STATIC, 4)
                                        ))
                                        .modifiers(List.of(ADVANTAGE))
                                        .build())
                                .rawText("Derek Heath: [ATTACK (R)] Fire Bolt [ADV] [DROPPED 4] [g20+4 = 12]\n[Attack #1 (R)] Fire Bolt [12] -&#62; [at Warhorse Skeleton] [MISS]")
                                .mainActor("Derek Heath")
                                .targets(List.of(
                                        ChatLogEntry.ActionTarget.builder()
                                                .actionResult(ChatLogEntry.ActionResult.MISS)
                                                .targetName("Warhorse Skeleton")
                                                .build()
                                ))
                                .build()
                )
        );
    }
}