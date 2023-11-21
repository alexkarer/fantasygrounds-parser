package at.karer.fantasygroundsparser.fantasygrounds.parser;

import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry.DieType.*;
import static org.assertj.core.api.Assertions.assertThat;

class ParserUtilsTest {
    @ParameterizedTest
    @MethodSource("provideDiceExpressions")
    void testDiceExpressionParsing(String diceExpression,  ChatLogEntry.DiceRollResult expectedResult) {

        var result = ParserUtils.getDiceRollResult(diceExpression);

        assertThat(result.build()).isEqualTo(expectedResult);
    }

    static Stream<Arguments> provideDiceExpressions() {
        return Stream.of(
                Arguments.of(
                        "[3d6 = 11]",
                        ChatLogEntry.DiceRollResult.builder()
                                .resultTotal(11)
                                .dice(List.of(
                                        new ChatLogEntry.Die(D6, 3)
                                ))
                                .build()
                ),
                Arguments.of(
                        "[3 = 3]",
                        ChatLogEntry.DiceRollResult.builder()
                                .resultTotal(3)
                                .dice(List.of(
                                        new ChatLogEntry.Die(STATIC, 3)
                                ))
                                .build()
                ),
                Arguments.of(
                        "[d6+g6+5 = 12]",
                        ChatLogEntry.DiceRollResult.builder()
                                .resultTotal(12)
                                .dice(List.of(
                                        new ChatLogEntry.Die(D6, 1),
                                        new ChatLogEntry.Die(D6, 1),
                                        new ChatLogEntry.Die(STATIC, 5)
                                ))
                                .build()
                ),
                Arguments.of(
                        "[d4+g4+d8+d6+g10+d12+12 = 30]",
                        ChatLogEntry.DiceRollResult.builder()
                                .resultTotal(30)
                                .dice(List.of(
                                        new ChatLogEntry.Die(D4, 1),
                                        new ChatLogEntry.Die(D4, 1),
                                        new ChatLogEntry.Die(D8, 1),
                                        new ChatLogEntry.Die(D6, 1),
                                        new ChatLogEntry.Die(D10, 1),
                                        new ChatLogEntry.Die(D12, 1),
                                        new ChatLogEntry.Die(STATIC, 12)
                                ))
                                .build()
                ),
                Arguments.of(
                        "[32d6+4d7+4 = 80]",
                        ChatLogEntry.DiceRollResult.builder()
                                .resultTotal(80)
                                .dice(List.of(
                                        new ChatLogEntry.Die(D6, 32),
                                        new ChatLogEntry.Die(OTHER, 4),
                                        new ChatLogEntry.Die(STATIC, 4)
                                ))
                                .build()
                ),
                Arguments.of(
                        "[r20+1 = 5]",
                        ChatLogEntry.DiceRollResult.builder()
                                .resultTotal(5)
                                .dice(List.of(
                                        new ChatLogEntry.Die(D20, 1),
                                        new ChatLogEntry.Die(STATIC, 1)
                                ))
                                .build()
                ),
                Arguments.of(
                        "[ = 5]",
                        ChatLogEntry.DiceRollResult.builder()
                                .resultTotal(5)
                                .dice(List.of(
                                        new ChatLogEntry.Die(STATIC, 5)
                                ))
                                .build()
                )
        );
    }
}