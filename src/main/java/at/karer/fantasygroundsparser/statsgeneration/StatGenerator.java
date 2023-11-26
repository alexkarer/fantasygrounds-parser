package at.karer.fantasygroundsparser.statsgeneration;

import at.karer.fantasygroundsparser.fantasygrounds.model.CharacterSheet;
import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;
import at.karer.fantasygroundsparser.fantasygrounds.model.FantasyGroundsDB;
import at.karer.fantasygroundsparser.statsgeneration.mapper.CharacterInfoMapper;
import at.karer.fantasygroundsparser.statsgeneration.mapper.DamageMapper;
import at.karer.fantasygroundsparser.statsgeneration.model.CampaignStatistics;
import at.karer.fantasygroundsparser.statsgeneration.model.CampaignStatistics.CharacterStats.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class StatGenerator {

    public static CampaignStatistics generateStats(FantasyGroundsDB fgDB, List<ChatLogEntry> chatLogEntries) {
        var characterNames = fgDB.characterSheets().keySet();

        var chatLogPerCharactersMainActor = chatLogEntries.stream()
                .filter(chatLog -> chatLog.mainActor() != null)
                .filter(chatLog -> characterNames.contains(chatLog.mainActor()))
                .collect(Collectors.groupingBy(ChatLogEntry::mainActor));

        var chatLogsPerCharactersTargeted = chatLogEntries.stream()
                .filter(chatLog -> chatLog.targets() != null)
                .flatMap(chatLog -> chatLog.targets().stream()
                        .map(target -> ChatLogEntry.builder()
                                .mainActor(chatLog.mainActor())
                                .type(chatLog.type())
                                .actionResult(chatLog.actionResult())
                                .targets(List.of(target))
                                .diceRollResult(chatLog.diceRollResult())
                                .abilityName(chatLog.abilityName())
                                .effectType(chatLog.effectType())
                                .rawChatlogs(chatLog.rawChatlogs())
                                .rawText(chatLog.rawText())
                                .build()))
                .filter(chatLog -> chatLog.targets().stream()
                        .filter(target -> target.targetName() != null)
                        .anyMatch(target -> characterNames.contains(target.targetName()))
                )
                .collect(Collectors.groupingBy(chatLog -> chatLog.targets().get(0).targetName()));

        var characterStats = new ArrayList<CampaignStatistics.CharacterStats>();
        for (var charSheet : fgDB.characterSheets().values()) {
            var chatLogsMainActor = chatLogPerCharactersMainActor.get(charSheet.name());
            var chatLogsTargeted = chatLogsPerCharactersTargeted.get(charSheet.name());
            if (chatLogsMainActor == null || chatLogsTargeted == null) {
                log.warn("Not able to find chatLogs sheet for: {}, skipping ...", charSheet.name());
            } else {
                characterStats.add(generateCharacterStats(charSheet, chatLogsMainActor, chatLogsTargeted, characterNames));
            }
        }

        return new CampaignStatistics(characterStats, null);
    }

    private static CampaignStatistics.CharacterStats generateCharacterStats(CharacterSheet charSheet,
                                                                            List<ChatLogEntry> chatLogsMainActor,
                                                                            List<ChatLogEntry> chatLogsTargeted,
                                                                            Set<String> charNames) {
        var chatLogsMainActorPerType = chatLogsMainActor.stream()
                .filter(chatLogEntry -> chatLogEntry.type() != null)
                .collect(Collectors.groupingBy(ChatLogEntry::type));
        var chatLogsTargetedPerType = chatLogsTargeted.stream()
                .filter(chatLogEntry -> chatLogEntry.type() != null)
                .collect(Collectors.groupingBy(ChatLogEntry::type));

        var attackRollsMade = getAttackRolls(
                chatLogsMainActorPerType.getOrDefault(ChatLogEntry.ChatLogEntryType.ATTACK, List.of())
        );
        var attackRollsReceived = getAttackRolls(
                chatLogsTargetedPerType.getOrDefault(ChatLogEntry.ChatLogEntryType.ATTACK, List.of())
        );

        var damageDone = getDamage(
                chatLogsMainActorPerType.getOrDefault(ChatLogEntry.ChatLogEntryType.DAMAGE, List.of())
        );
        var damageReceived = getDamage(
                chatLogsTargetedPerType.getOrDefault(ChatLogEntry.ChatLogEntryType.DAMAGE, List.of())
        );

        var healingDone = getHealingDone(
                chatLogsMainActorPerType.getOrDefault(ChatLogEntry.ChatLogEntryType.HEAL, List.of()), charNames
        );
        var healingReceived = getHealingReceived(
                chatLogsTargetedPerType.getOrDefault(ChatLogEntry.ChatLogEntryType.HEAL, List.of())
        );

        var savingThrowsMade = getSavingThrowsMade(
                chatLogsTargetedPerType.getOrDefault(ChatLogEntry.ChatLogEntryType.SAVE, List.of())
        );

        var deathSavingThrows = getDeathSavingThrows(
                chatLogsMainActorPerType.getOrDefault(ChatLogEntry.ChatLogEntryType.DEATH_SAVE, List.of())
        );


        return new CampaignStatistics.CharacterStats(
                CharacterInfoMapper.INSTANCE.toCharacterInfo(charSheet),
                attackRollsMade,
                attackRollsReceived,
                damageDone,
                damageReceived,
                healingDone,
                healingReceived,
                savingThrowsMade,
                deathSavingThrows
        );
    }

    private static AttackRolls getAttackRolls(List<ChatLogEntry> attackRollChatLogs) {
        var attackRollsByActionResult = attackRollChatLogs.stream()
                .filter(chatLog -> chatLog.targets() != null && !chatLog.targets().isEmpty())
                .flatMap(chatLog -> chatLog.targets().stream())
                .collect(Collectors.groupingBy(ChatLogEntry.ActionTarget::actionResult));

        var criticalHits = attackRollsByActionResult.getOrDefault(ChatLogEntry.ActionResult.HIT_CRITICAL, List.of()).size();
        var criticalMisses = attackRollsByActionResult.getOrDefault(ChatLogEntry.ActionResult.MISS_CRITICAL, List.of()).size();
        var attacksHit = attackRollsByActionResult.getOrDefault(ChatLogEntry.ActionResult.HIT, List.of()).size() + criticalHits;
        var attacksMissed = attackRollsByActionResult.getOrDefault(ChatLogEntry.ActionResult.MISS, List.of()).size() + criticalMisses;
        return new AttackRolls(
                attacksHit, attacksMissed, criticalHits, criticalMisses
        );
    }

    private static Map<Damage.DamageType, Damage> getDamage(List<ChatLogEntry> damageChatLogs) {
        return damageChatLogs.stream()
                .flatMap(chatLog -> chatLog.targets().stream()
                        .flatMap(target -> target.damage().stream()
                                .map(damage -> Pair.of(
                                        DamageMapper.INSTANCE.toStatsDamageType(damage.type()),
                                        DamageMapper.INSTANCE.toStatsDamage(damage))
                                )
                        )
                )
                .collect(Collectors.groupingBy(Pair::getKey))
                .entrySet().stream()
                .map(damageEntry -> Pair.of(
                        damageEntry.getKey(),
                        damageEntry.getValue().stream()
                                .map(Pair::getValue)
                                .reduce((d1, d2) -> new Damage(
                                        d1.damageDone() + d2.damageDone(),
                                        d1.damageResisted() + d2.damageResisted(),
                                        d1.overkillDamage() + d2.overkillDamage()
                                )).orElse(new Damage(0, 0, 0))
                )).collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    private static List<Healing> getHealingDone(List<ChatLogEntry> healingDoneChatLogs, Set<String> charNames) {
        var healingPerTarget = healingDoneChatLogs.stream()
                .flatMap(chatLogEntry -> chatLogEntry.targets().stream())
                .collect(Collectors.groupingBy(target ->
                        charNames.contains(target.targetName()) ? target.targetName() : "others")
                );

        return healingPerTarget.entrySet().stream()
                .map(healingEntry -> new Healing(
                        healingEntry.getKey(),
                        healingEntry.getValue().stream().mapToInt(target -> target.diceRollResult().resultTotal()).sum()
                )).sorted((h1, h2) -> {
                    if ("others".equals(h1.target())) {
                        return 1;
                    } else if ("others".equals(h2.target())) {
                        return -1;
                    } else {
                        return h1.target().compareTo(h2.target());
                    }
                })
                .toList();
    }

    private static int getHealingReceived(List<ChatLogEntry> healingReceivedChatLogs) {
        return healingReceivedChatLogs.stream()
                .flatMapToInt(chatLog -> chatLog.targets().stream()
                        .mapToInt(target -> target.diceRollResult().resultTotal())
                ).sum();
    }

    private static Map<String, SavingThrow> getSavingThrowsMade(List<ChatLogEntry> savingThrowMadeChatLogs) {
        return savingThrowMadeChatLogs.stream()
                .map(chatLog -> Pair.of(
                        chatLog.abilityName(),
                        new SavingThrow(
                                ChatLogEntry.ActionResult.SAVED == chatLog.targets().get(0).actionResult() ? 1 : 0,
                                ChatLogEntry.ActionResult.FAILED == chatLog.targets().get(0).actionResult() ? 1 : 0)
                )).collect(Collectors.groupingBy(Pair::getKey))
                .entrySet().stream()
                .map(savingThrowEntry -> Pair.of(
                        savingThrowEntry.getKey(),
                        savingThrowEntry.getValue().stream()
                                .map(Pair::getValue)
                                .reduce((s1, s2) -> new SavingThrow(s1.succeeded() + s2.succeeded(), s1.failed() + s2.failed()))
                                .orElse(new SavingThrow(0, 0))
                ))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    private static DeathSavingThrows getDeathSavingThrows(List<ChatLogEntry> deathSavingThrowChatLogs) {
        var deathSaveByActionResult = deathSavingThrowChatLogs.stream()
                .filter(chatLog -> chatLog.actionResult() != null)
                .collect(Collectors.groupingBy(ChatLogEntry::actionResult));

        var criticalSuccesses = deathSaveByActionResult.getOrDefault(ChatLogEntry.ActionResult.SAVE_CRITICAL, List.of()).size();
        var criticalFails = deathSaveByActionResult.getOrDefault(ChatLogEntry.ActionResult.FAIL_CRITICAL, List.of()).size();
        var successes = deathSaveByActionResult.getOrDefault(ChatLogEntry.ActionResult.SAVED, List.of()).size() + criticalSuccesses;
        var fails = deathSaveByActionResult.getOrDefault(ChatLogEntry.ActionResult.FAILED, List.of()).size() + criticalFails;
        return new DeathSavingThrows(
                successes, fails, criticalSuccesses, criticalFails
        );
    }

}
