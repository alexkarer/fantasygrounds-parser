package at.karer.fantasygroundsparser;

import at.karer.fantasygroundsparser.commandline.CommandLineArgs;
import at.karer.fantasygroundsparser.fantasygrounds.deserializer.FantasyGroundsDbDeserializer;
import at.karer.fantasygroundsparser.fantasygrounds.parser.FantasyGroundsChatLogParser;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Parser {

    public static void parse(CommandLineArgs args) {
        var fantasyGroundsDBFuture = CompletableFuture.supplyAsync(() ->
                FantasyGroundsDbDeserializer.deserializeDB(args.getCampaignFolder())
        );
        var chatLogEntriesFuture = CompletableFuture.supplyAsync(() ->
                FantasyGroundsChatLogParser.parseChatLog(args.getCampaignFolder())
        );

        try {
            System.out.printf("""
                    Character Sheets: %s
                    Amount of Chatlogs: %s
                    """,
                    fantasyGroundsDBFuture.get().getCharacterSheets(),
                    chatLogEntriesFuture.get().size());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
