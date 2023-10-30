package at.karer.fantasygroundsparser.fantasygrounds.parser;

import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry.ActionResult.DAMAGE;
import static at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry.ActionResult.KILLING_BLOW;
import static at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry.DamageType.*;
import static at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry.DieType.*;
import static org.assertj.core.api.Assertions.assertThat;

class DamageParserTest {

    @ParameterizedTest
    @MethodSource("provideDamageChatLogs")
    void testDamageParsing(List<String> filteredChatlog, ChatLogEntry expectedResult) {

        var result = DamageParser.createDamageEntry(filteredChatlog, 0);

        assertThat(result).isEqualTo(expectedResult);
    }

    static Stream<Arguments> provideDamageChatLogs() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                "Jack Heart: [DAMAGE (R)] Crossbow, Light [TYPE: piercing (d8+2=10)] [d8+2 = 10]"
                        ),
                        null
                ),
                // Standard damage done
                Arguments.of(
                        List.of(
                                "Jack Heart: [DAMAGE (R)] Crossbow, Light [TYPE: piercing (d8+2=10)] [d8+2 = 10]",
                                "Damage [10] -&#62; [to Giant Goat] [STATUS: Heavy]"
                        ),
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.DAMAGE)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(10)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(D8, 1),
                                                new ChatLogEntry.Die(STATIC, 2)
                                        ))
                                        .modifiers(List.of())
                                        .build())
                                .rawText("Jack Heart: [DAMAGE (R)] Crossbow, Light [TYPE: piercing (d8+2=10)] [d8+2 = 10]\nDamage [10] -&#62; [to Giant Goat] [STATUS: Heavy]")
                                .mainActor("Jack Heart")
                                .targets(List.of(
                                        ChatLogEntry.ActionTarget.builder()
                                                .actionResult(DAMAGE)
                                                .damage(ChatLogEntry.Damage.builder()
                                                        .damageDone(10)
                                                        .damageType(Set.of(PIERCING))
                                                        .build())
                                                .targetName("Giant Goat")
                                                .build()
                                ))
                                .rawChatlogs(2)
                                .build()
                ),
                // Critical damage done
                Arguments.of(
                        List.of(
                                "Giant Wolf Spider 1: [DAMAGE (M)] Bite [CRITICAL] [TYPE: piercing (d6+1=3)] [TYPE: piercing,critical (d6=3)] [d6+g6+1 = 6]",
                                "Damage [6] -&#62; [to Srenga Tempest] [STATUS: Wounded]"
                        ),
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.DAMAGE)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(6)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(D6, 1),
                                                new ChatLogEntry.Die(D6, 1),
                                                new ChatLogEntry.Die(STATIC, 1)
                                        ))
                                        .modifiers(List.of())
                                        .build())
                                .rawText("Giant Wolf Spider 1: [DAMAGE (M)] Bite [CRITICAL] [TYPE: piercing (d6+1=3)] [TYPE: piercing,critical (d6=3)] [d6+g6+1 = 6]\nDamage [6] -&#62; [to Srenga Tempest] [STATUS: Wounded]")
                                .mainActor("Giant Wolf Spider 1")
                                .targets(List.of(
                                        ChatLogEntry.ActionTarget.builder()
                                                .actionResult(DAMAGE)
                                                .damage(ChatLogEntry.Damage.builder()
                                                        .damageDone(6)
                                                        .damageType(Set.of(PIERCING))
                                                        .build())
                                                .targetName("Srenga Tempest")
                                                .build()
                                ))
                                .rawChatlogs(2)
                                .build()
                ),
                // Killing Blow
                Arguments.of(
                        List.of(
                                "Jack Heart: [DAMAGE (M)] Longsword [TYPE: slashing (d8+3=11)] [d8+3 = 11]",
                                "Effect ['Unconscious'] -&#62; [to Duergar Apprentice 1]",
                                "Effect ['Prone'] -&#62; [to Duergar Apprentice 1]",
                                "Damage [11] -&#62; [to Duergar Apprentice 1] [STATUS: Dying]"
                        ),
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.DAMAGE)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(11)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(D8, 1),
                                                new ChatLogEntry.Die(STATIC, 3)
                                        ))
                                        .modifiers(List.of())
                                        .build())
                                .rawText("Jack Heart: [DAMAGE (M)] Longsword [TYPE: slashing (d8+3=11)] [d8+3 = 11]\nDamage [11] -&#62; [to Duergar Apprentice 1] [STATUS: Dying]")
                                .mainActor("Jack Heart")
                                .targets(List.of(
                                        ChatLogEntry.ActionTarget.builder()
                                                .actionResult(KILLING_BLOW)
                                                .damage(ChatLogEntry.Damage.builder()
                                                        .damageDone(11)
                                                         .damageType(Set.of(SLASHING))
                                                        .build())
                                                .targetName("Duergar Apprentice 1")
                                                .build()
                                ))
                                .rawChatlogs(4)
                                .build()
                ),
                // Partially resisted
                Arguments.of(
                        List.of(
                                "Troglodyte 1: [DAMAGE (M)] Claw [TYPE: slashing (1d4+2=5)] [d4+2 = 5]",
                                "[Damage (M)] Claw [4] -&#62; [to Jack Heart ] [PARTIALLY RESISTED]"
                        ),
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.DAMAGE)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(5)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(D4, 1),
                                                new ChatLogEntry.Die(STATIC, 2)
                                        ))
                                        .modifiers(List.of())
                                        .build())
                                .rawText("Troglodyte 1: [DAMAGE (M)] Claw [TYPE: slashing (1d4+2=5)] [d4+2 = 5]\n[Damage (M)] Claw [4] -&#62; [to Jack Heart ] [PARTIALLY RESISTED]")
                                .mainActor("Troglodyte 1")
                                .targets(List.of(
                                        ChatLogEntry.ActionTarget.builder()
                                                .actionResult(DAMAGE)
                                                .damage(ChatLogEntry.Damage.builder()
                                                        .damageDone(4)
                                                         .damageType(Set.of(SLASHING))
                                                        .build())
                                                .targetName("Jack Heart")
                                                .build()
                                ))
                                .rawChatlogs(2)
                                .build()
                ),
                // fully resisted
                Arguments.of(
                        List.of(
                                "Brown Bear: [DAMAGE (M)] Claws [TYPE: slashing (2d6+4=10)] [2d6+4 = 10]",
                                "[Damage (M)] Claws [0] -&#62; [to Werebear] [RESISTED]"
                        ),
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.DAMAGE)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(10)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(D6, 2),
                                                new ChatLogEntry.Die(STATIC, 4)
                                        ))
                                        .modifiers(List.of())
                                        .build())
                                .rawText("Brown Bear: [DAMAGE (M)] Claws [TYPE: slashing (2d6+4=10)] [2d6+4 = 10]\n[Damage (M)] Claws [0] -&#62; [to Werebear] [RESISTED]")
                                .mainActor("Brown Bear")
                                .targets(List.of(
                                        ChatLogEntry.ActionTarget.builder()
                                                .actionResult(DAMAGE)
                                                .damage(ChatLogEntry.Damage.builder()
                                                        .damageDone(0)
                                                        .damageType(Set.of(SLASHING))
                                                        .build())
                                                .targetName("Werebear")
                                                .build()
                                ))
                                .rawChatlogs(2)
                                .build()
                ),
                // multi target damage with killing blows
                Arguments.of(
                        List.of(
                                "Derek Heath: [DAMAGE] Fireball [TYPE: fire (1d8+8d6=38)] [8d6+d8 = 38]",
                                "Effect ['Unconscious'] -&#62; [to Drow Warrior 2]",
                                "Effect ['Prone'] -&#62; [to Drow Warrior 2]",
                                "[Damage] Fireball [38] -&#62; [to Drow Warrior 2] [STATUS: Dying]",
                                "[Damage] Fireball [38] -&#62; [to Drow Warrior 1] [STATUS: Heavy]",
                                "[Damage] Fireball [19] -&#62; [to Drow Warrior 4] [HALF] [STATUS: Wounded]",
                                "Effect ['Unconscious'] -&#62; [to Drow Warrior 3]",
                                "Effect ['Prone'] -&#62; [to Drow Warrior 3]",
                                "[Damage] Fireball [38] -&#62; [to Drow Warrior 3] [DAMAGE EXCEEDS HIT POINTS BY 15] [STATUS: Dying]",
                                "[Damage] Fireball [19] -&#62; [to Giant Spider 2] [HALF] [STATUS: Heavy]",
                                "Effect ['Unconscious'] -&#62; [to Giant Spider 3]",
                                "Effect ['Prone'] -&#62; [to Giant Spider 3]",
                                "[Damage] Fireball [38] -&#62; [to Giant Spider 3] [DAMAGE EXCEEDS HIT POINTS BY 10] [STATUS: Dying]"
                        ),
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.DAMAGE)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(38)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(D6, 8),
                                                new ChatLogEntry.Die(D8, 1)
                                        ))
                                        .modifiers(List.of())
                                        .build())
                                .rawText("Derek Heath: [DAMAGE] Fireball [TYPE: fire (1d8+8d6=38)] [8d6+d8 = 38]\n[Damage] Fireball [38] -&#62; [to Drow Warrior 2] [STATUS: Dying]\n[Damage] Fireball [38] -&#62; [to Drow Warrior 1] [STATUS: Heavy]\n[Damage] Fireball [19] -&#62; [to Drow Warrior 4] [HALF] [STATUS: Wounded]\n[Damage] Fireball [38] -&#62; [to Drow Warrior 3] [DAMAGE EXCEEDS HIT POINTS BY 15] [STATUS: Dying]\n[Damage] Fireball [19] -&#62; [to Giant Spider 2] [HALF] [STATUS: Heavy]\n[Damage] Fireball [38] -&#62; [to Giant Spider 3] [DAMAGE EXCEEDS HIT POINTS BY 10] [STATUS: Dying]")
                                .mainActor("Derek Heath")
                                .targets(List.of(
                                        ChatLogEntry.ActionTarget.builder()
                                                .actionResult(KILLING_BLOW)
                                                .damage(ChatLogEntry.Damage.builder()
                                                        .damageDone(38)
                                                        .damageType(Set.of(FIRE))
                                                        .build())
                                                .targetName("Drow Warrior 2")
                                                .build(),
                                        ChatLogEntry.ActionTarget.builder()
                                                .actionResult(DAMAGE)
                                                .damage(ChatLogEntry.Damage.builder()
                                                        .damageDone(38)
                                                        .damageType(Set.of(FIRE))
                                                        .build())
                                                .targetName("Drow Warrior 1")
                                                .build(),
                                        ChatLogEntry.ActionTarget.builder()
                                                .actionResult(DAMAGE)
                                                .damage(ChatLogEntry.Damage.builder()
                                                        .damageDone(19)
                                                        .damageType(Set.of(FIRE))
                                                        .build())
                                                .targetName("Drow Warrior 4")
                                                .build(),
                                        ChatLogEntry.ActionTarget.builder()
                                                .actionResult(KILLING_BLOW)
                                                .damage(ChatLogEntry.Damage.builder()
                                                        .damageDone(38)
                                                        .damageType(Set.of(FIRE))
                                                        .overkillDamage(15)
                                                        .build())
                                                .targetName("Drow Warrior 3")
                                                .build(),
                                        ChatLogEntry.ActionTarget.builder()
                                                .actionResult(DAMAGE)
                                                .damage(ChatLogEntry.Damage.builder()
                                                        .damageDone(19)
                                                        .damageType(Set.of(FIRE))
                                                        .build())
                                                .targetName("Giant Spider 2")
                                                .build(),
                                        ChatLogEntry.ActionTarget.builder()
                                                .actionResult(KILLING_BLOW)
                                                .damage(ChatLogEntry.Damage.builder()
                                                        .damageDone(38)
                                                        .overkillDamage(10)
                                                        .damageType(Set.of(FIRE))
                                                        .build())
                                                .targetName("Giant Spider 3")
                                                .build()
                                ))
                                .rawChatlogs(13)
                                .build()
                ),
                // Multiple damage types
                Arguments.of(
                        List.of(
                                "Ashton Kr&#252;ger: [DAMAGE (M)] Mayar  GoBD/GWM  -5 (2H) [EFFECTS 1d8] [TYPE: piercing,magic (1d8+1d10+15=22)] [TYPE: acid (1d6=1)] [d10+d6+p8+15 = 23]",
                                "[Damage (M)] Mayar  GoBD/GWM  -5 (2H) [23] -&#62; [to Xanathar Thug 2] [STATUS: Heavy]"
                        ),
                        ChatLogEntry.builder()
                                .type(ChatLogEntry.ChatLogEntryType.DAMAGE)
                                .diceRollResult(ChatLogEntry.DiceRollResult.builder()
                                        .resultTotal(23)
                                        .dice(List.of(
                                                new ChatLogEntry.Die(D10, 1),
                                                new ChatLogEntry.Die(D6, 1),
                                                new ChatLogEntry.Die(D8, 1),
                                                new ChatLogEntry.Die(STATIC, 15)
                                        ))
                                        .modifiers(List.of())
                                        .build())
                                .rawText("Ashton Kr&#252;ger: [DAMAGE (M)] Mayar  GoBD/GWM  -5 (2H) [EFFECTS 1d8] [TYPE: piercing,magic (1d8+1d10+15=22)] [TYPE: acid (1d6=1)] [d10+d6+p8+15 = 23]\n[Damage (M)] Mayar  GoBD/GWM  -5 (2H) [23] -&#62; [to Xanathar Thug 2] [STATUS: Heavy]")
                                .mainActor("Ashton Kr&#252;ger")
                                .targets(List.of(
                                        ChatLogEntry.ActionTarget.builder()
                                                .actionResult(DAMAGE)
                                                .damage(ChatLogEntry.Damage.builder()
                                                        .damageDone(23)
                                                        .damageType(Set.of(PIERCING, ACID))
                                                        .build())
                                                .targetName("Xanathar Thug 2")
                                                .build()
                                ))
                                .rawChatlogs(2)
                                .build()
                )
        );
    }
}