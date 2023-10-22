package at.karer.fantasygroundsparser;

import at.karer.fantasygroundsparser.commandline.CommandLineArgs;
import at.karer.fantasygroundsparser.fantasygrounds.deserializer.FantasyGroundsDbDeserializer;

public class Parser {

    public static void parse(CommandLineArgs args) {
        var fantasyGroundsDB = FantasyGroundsDbDeserializer.deserializeDB(args.getCampaignFolder());
        System.out.printf("""
                Character Sheets: %s
                """, fantasyGroundsDB.getCharacterSheets());
    }

}
