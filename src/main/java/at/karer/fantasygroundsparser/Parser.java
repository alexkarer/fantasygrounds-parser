package at.karer.fantasygroundsparser;

import at.karer.fantasygroundsparser.commandline.CommandLineArgs;
import at.karer.fantasygroundsparser.fantasygrounds.deserializer.FantasyGroundsDbDeserializer;
import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;
import at.karer.fantasygroundsparser.fantasygrounds.parser.FantasyGroundsChatLogParser;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
public class Parser {

    public static void parse(CommandLineArgs args) {
        var fantasyGroundsDBFuture = CompletableFuture.supplyAsync(() ->
                FantasyGroundsDbDeserializer.deserializeDB(args.getCampaignFolder())
        );
        var chatLogEntriesFuture = CompletableFuture.supplyAsync(() ->
                FantasyGroundsChatLogParser.parseChatLog(args.getCampaignFolder())
        );

        try {
            var fantasyGroundsDB = fantasyGroundsDBFuture.get();
            var chatLogEntries = chatLogEntriesFuture.get();

            var characterNames = fantasyGroundsDB.characterSheets().keySet();

            var chatLogPerCharacters = chatLogEntries.stream()
                    .filter(chatLog -> chatLog.mainActor() != null)
                    .filter(chatLog -> characterNames.contains(chatLog.mainActor()))
                    .collect(Collectors.groupingBy(ChatLogEntry::mainActor));

            for (var chatLogPerCharacter : chatLogPerCharacters.entrySet()) {
                var charSheet = fantasyGroundsDB.characterSheets().get(chatLogPerCharacter.getKey());
                if (charSheet == null) {
                    log.warn("Not able to find character sheet for: {} in fantasygrounds DB, skipping ...", chatLogPerCharacter.getKey());
                    continue;
                }

                var chatLogsPerType = chatLogPerCharacter.getValue().stream()
                        .filter(chatLogEntry -> chatLogEntry.type() != null)
                        .collect(Collectors.groupingBy(ChatLogEntry::type));

                System.out.println("*******************************************************************");
                System.out.printf("%s, Level: %d, Race: %s\n", charSheet.name(), charSheet.level(), charSheet.race());
                System.out.println("*******************************************************************");
                printAttackRollResults(chatLogsPerType.getOrDefault(ChatLogEntry.ChatLogEntryType.ATTACK, List.of()));

                System.out.println();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static void printAttackRollResults(List<ChatLogEntry> attackRollChatLogs) {
        var attackRollsByActionResult = attackRollChatLogs.stream()
                .filter(chatLog -> chatLog.targets() != null && !chatLog.targets().isEmpty())
                .flatMap(chatLog -> chatLog.targets().stream())
                .collect(Collectors.groupingBy(ChatLogEntry.ActionTarget::actionResult));

        var criticalHits = attackRollsByActionResult.getOrDefault(ChatLogEntry.ActionResult.HIT_CRITICAL, List.of()).size();
        var criticalMisses = attackRollsByActionResult.getOrDefault(ChatLogEntry.ActionResult.MISS_CRITICAL, List.of()).size();
        var attacksHit = attackRollsByActionResult.getOrDefault(ChatLogEntry.ActionResult.HIT, List.of()).size() + criticalHits;
        var attacksMissed = attackRollsByActionResult.getOrDefault(ChatLogEntry.ActionResult.MISS, List.of()).size() + criticalMisses;
        var attacksMade = criticalHits + criticalMisses + attacksHit + attacksMissed;


        System.out.println("ATTACK ROLLS:");
        System.out.printf("""
                        \tTotal Attacks made: %d
                        \tAttacks Hit: %d (%.2f%%)
                        \tAttacks Missed: %d (%.2f%%)
                        \tCritical Hits: %d (%.2f%%)
                        \tCritical Misses: %d (%.2f%%)%n""",
                attacksMade,
                attacksHit, ((double)attacksHit / attacksMade)*100,
                attacksMissed, ((double)attacksMissed / attacksMade)*100,
                criticalHits, ((double)criticalHits / attacksMade)*100,
                criticalMisses, ((double)criticalMisses / attacksMade)*100);
    }

}
