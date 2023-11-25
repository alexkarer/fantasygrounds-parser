package at.karer.fantasygroundsparser;

import at.karer.fantasygroundsparser.commandline.CommandLineArgs;
import at.karer.fantasygroundsparser.commandline.printer.StatsPrinter;
import at.karer.fantasygroundsparser.fantasygrounds.deserializer.FantasyGroundsDbDeserializer;
import at.karer.fantasygroundsparser.fantasygrounds.parser.FantasyGroundsChatLogParser;
import at.karer.fantasygroundsparser.statsgeneration.StatGenerator;
import com.beust.jcommander.JCommander;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
public class Main {
    public static void main(String[] args) {
        var arguments = new CommandLineArgs();
        JCommander.newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);

        var fantasyGroundsDBFuture = CompletableFuture.supplyAsync(() ->
                FantasyGroundsDbDeserializer.deserializeDB(arguments.getCampaignFolder())
        );
        var chatLogEntriesFuture = CompletableFuture.supplyAsync(() ->
                FantasyGroundsChatLogParser.parseChatLog(arguments.getCampaignFolder())
        );

        try {
            var fantasyGroundsDB = fantasyGroundsDBFuture.get();
            var chatLogEntries = chatLogEntriesFuture.get();

            var campaignStatistics = StatGenerator.generateStats(fantasyGroundsDB, chatLogEntries);
            StatsPrinter.printStatistics(campaignStatistics);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error during fantasygrounds file parsing: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}
