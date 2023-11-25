package at.karer.fantasygroundsparser.statsgeneration;

import at.karer.fantasygroundsparser.fantasygrounds.model.CharacterSheet;
import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;
import at.karer.fantasygroundsparser.fantasygrounds.model.FantasyGroundsDB;
import at.karer.fantasygroundsparser.statsgeneration.mapper.CharacterInfoMapper;
import at.karer.fantasygroundsparser.statsgeneration.model.CampaignStatistics;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
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
                characterStats.add(generateCharacterStats(charSheet, chatLogsMainActor, chatLogsTargeted));
            }
        }

        return new CampaignStatistics(characterStats, null);
    }

    private static CampaignStatistics.CharacterStats generateCharacterStats(CharacterSheet charSheet,
                                                                            List<ChatLogEntry> chatLogsMainActor,
                                                                            List<ChatLogEntry> chatLogsTargeted) {
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


        return new CampaignStatistics.CharacterStats(
                CharacterInfoMapper.INSTANCE.toCharacterInfo(charSheet),
                attackRollsMade,
                attackRollsReceived
        );
    }

    private static CampaignStatistics.CharacterStats.AttackRolls getAttackRolls(List<ChatLogEntry> attackRollChatLogs) {
        var attackRollsByActionResult = attackRollChatLogs.stream()
                .filter(chatLog -> chatLog.targets() != null && !chatLog.targets().isEmpty())
                .flatMap(chatLog -> chatLog.targets().stream())
                .collect(Collectors.groupingBy(ChatLogEntry.ActionTarget::actionResult));

        var criticalHits = attackRollsByActionResult.getOrDefault(ChatLogEntry.ActionResult.HIT_CRITICAL, List.of()).size();
        var criticalMisses = attackRollsByActionResult.getOrDefault(ChatLogEntry.ActionResult.MISS_CRITICAL, List.of()).size();
        var attacksHit = attackRollsByActionResult.getOrDefault(ChatLogEntry.ActionResult.HIT, List.of()).size() + criticalHits;
        var attacksMissed = attackRollsByActionResult.getOrDefault(ChatLogEntry.ActionResult.MISS, List.of()).size() + criticalMisses;
        var attacksMade = criticalHits + criticalMisses + attacksHit + attacksMissed;
        return new CampaignStatistics.CharacterStats.AttackRolls(
                attacksMade, attacksHit, attacksMissed, criticalHits, criticalMisses
        );
    }

}
