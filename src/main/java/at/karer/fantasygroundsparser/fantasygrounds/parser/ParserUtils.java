package at.karer.fantasygroundsparser.fantasygrounds.parser;

import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;

import java.util.ArrayList;
import java.util.List;

public class ParserUtils {

    static ChatLogEntry.DiceRollResult.DiceRollResultBuilder getDiceRollResult(String rawChatLog) {
        var indexDiceRollResult = rawChatLog.lastIndexOf("[");
        var diceExpression = rawChatLog.substring(indexDiceRollResult + 1, rawChatLog.length() - 1);

        var builder = ChatLogEntry.DiceRollResult.builder();
        var resultStartIndex = diceExpression.indexOf("=");
        var resultString = diceExpression.substring(resultStartIndex + 1).trim();
        builder.resultTotal(Integer.parseInt(resultString));

        String[] dice;
        if (diceExpression.startsWith(" ")) {
            dice = new String[]{resultString};
        } else {
            dice = diceExpression.substring(0, resultStartIndex).trim().split("[+-]");
        }

        var diceList = new ArrayList<ChatLogEntry.Die>();
        for (String die : dice) {
            diceList.add(parseSingleDiceExpression(die));
        }
        builder.dice(diceList);

        return builder;
    }

    private static ChatLogEntry.Die parseSingleDiceExpression(String singleDiceExpression) {
        if (Character.isDigit(singleDiceExpression.charAt(0))) {
            // e.g. 20d4
            if (singleDiceExpression.contains("d")) {
                var diceAmount = Integer.parseInt(singleDiceExpression.substring(0, singleDiceExpression.indexOf("d")));
                var diceType = parseDiceType(singleDiceExpression.substring(singleDiceExpression.indexOf("d")));
                return new ChatLogEntry.Die(diceType, diceAmount);
                // e.g. 10g6
            } else if (singleDiceExpression.contains("g")) {
                var diceAmount = Integer.parseInt(singleDiceExpression.substring(0, singleDiceExpression.indexOf("g")));
                var diceType = parseDiceType(singleDiceExpression.substring(singleDiceExpression.indexOf("g")));
                return new ChatLogEntry.Die(diceType, diceAmount);
            } else if (singleDiceExpression.contains("p")) {
                var diceAmount = Integer.parseInt(singleDiceExpression.substring(0, singleDiceExpression.indexOf("p")));
                var diceType = parseDiceType(singleDiceExpression.substring(singleDiceExpression.indexOf("p")));
                return new ChatLogEntry.Die(diceType, diceAmount);
            } else if (singleDiceExpression.contains("r")) {
                var diceAmount = Integer.parseInt(singleDiceExpression.substring(0, singleDiceExpression.indexOf("r")));
                var diceType = parseDiceType(singleDiceExpression.substring(singleDiceExpression.indexOf("r")));
                return new ChatLogEntry.Die(diceType, diceAmount);
            } else {
                return new ChatLogEntry.Die(ChatLogEntry.DieType.STATIC, Integer.parseInt(singleDiceExpression));
            }
        } else if (singleDiceExpression.startsWith("d") || singleDiceExpression.startsWith("g") || singleDiceExpression.startsWith("p") || singleDiceExpression.startsWith("r")) {
            return new ChatLogEntry.Die(parseDiceType(singleDiceExpression), 1);
        }
        return null;
    }

    private static ChatLogEntry.DieType parseDiceType(String diceString) {
        var diceNumber = diceString.substring(1);
        switch (diceNumber) {
            case "4" -> {
                return ChatLogEntry.DieType.D4;
            }
            case "6" -> {
                return ChatLogEntry.DieType.D6;
            }
            case "8" -> {
                return ChatLogEntry.DieType.D8;
            }
            case "10" -> {
                return ChatLogEntry.DieType.D10;
            }
            case "12" -> {
                return ChatLogEntry.DieType.D12;
            }
            case "20" -> {
                return ChatLogEntry.DieType.D20;
            }
            case "100" -> {
                return ChatLogEntry.DieType.D100;
            }
            default -> {
                return ChatLogEntry.DieType.OTHER;
            }
        }
    }

    static List<ChatLogEntry.Modifiers> addModifiers(String chatLog) {
        var modifiers = new ArrayList<ChatLogEntry.Modifiers>();
        if (chatLog.contains("[ADV]")) {
            modifiers.add(ChatLogEntry.Modifiers.ADVANTAGE);
        }
        if (chatLog.contains("[DIS")) {
            modifiers.add(ChatLogEntry.Modifiers.DISADVANTAGE);
        }
        if (chatLog.contains("[+2]")) {
            modifiers.add(ChatLogEntry.Modifiers.PLUS2);
        }
        if (chatLog.contains("[+5]")) {
            modifiers.add(ChatLogEntry.Modifiers.PLUS5);
        }
        if (chatLog.contains("[-2]")) {
            modifiers.add(ChatLogEntry.Modifiers.MINUS2);
        }
        if (chatLog.contains("[-5]")) {
            modifiers.add(ChatLogEntry.Modifiers.MINUS5);
        }
        return modifiers;
    }

    static String getMainActorName(String rawChatLog) {
        var indexMainActor = rawChatLog.indexOf(":");
        return rawChatLog.substring(0, indexMainActor).trim();
    }

    static String getAbilityName(String rawChatLog) {
        var startIndex = rawChatLog.indexOf(']') + 1;
        var endIndex = rawChatLog.indexOf('[', startIndex);
        return rawChatLog.substring(startIndex, endIndex).trim();
    }
}
