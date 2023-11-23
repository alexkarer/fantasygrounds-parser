package at.karer.fantasygroundsparser;

import at.karer.fantasygroundsparser.commandline.CommandLineArgs;
import com.beust.jcommander.JCommander;

public class Main {
    public static void main(String[] args) {
        var argumets = new CommandLineArgs();
        JCommander.newBuilder()
                .addObject(argumets)
                .build()
                .parse(args);

        Parser.parse(argumets);
    }
}
