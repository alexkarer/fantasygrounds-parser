package at.karer.fantasygroundsparser.fantasygrounds.parser;

import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;

import java.util.*;
import java.util.regex.Pattern;

import static at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry.DamageType.*;

public class DamageParser {

    private static final Pattern PATTERN_DAMAGE_DONE = Pattern.compile("\\[\\d+\\]");

    /**
     * Transforms raw chatlog text about damage applied into structured data
     * Damage consists usually of 2 entries: First about the damage roll, Second information about how much damage was done
     * It is possible for damage to be rolled without a target => these will be ignored
     *  Damage Roll Format: MainActor ": " "[DAMAGE [" (R)"|" (M)"] "] " AttackName {" [EFFECTS " DiceExpression "]"} " [TYPE: " DamageType " (" DiceExpression ")] " {" [TYPE: " DamageType " (" DiceExpression ")] "} " [" DiceExpression "]"
     *  Damage applied Format: "[Damage" [" (M)"|" (R)"] "] " AttackName "[" Number "]" "[to " TargetName "] " "[STATUS: " ("Wounded"|"Heavy"|"Dying") "]"
     * @param filteredChatLogs all filtered chatlogs of a campaign
     * @param index current index of the chatlogs
     * @return ChatLogEntry with all information fit in for damage done
     */
    static ChatLogEntry createDamageEntry(List<String> filteredChatLogs, int index) {
        var damageRolledChatLog = filteredChatLogs.get(index);
        var targetDamageAppliedChatLogs = new ArrayList<String>();
        int rawChatLogs = 1;

        for (int i = index + 1;
             i < filteredChatLogs.size() &&
                     (filteredChatLogs.get(i).contains("Damage") ||
                     filteredChatLogs.get(i).contains("Effect "));
             i++) {
            if (filteredChatLogs.get(i).contains("Damage")) {
                targetDamageAppliedChatLogs.add(filteredChatLogs.get(i));
            }
            rawChatLogs++;
        }
        // if next entry is not damage applied this means the damage had no target => ignore
        if (targetDamageAppliedChatLogs.isEmpty()) {
            return null;
        }

        var builder = ChatLogEntry.builder();

        builder.type(ChatLogEntry.ChatLogEntryType.DAMAGE);
        builder.rawChatlogs(rawChatLogs);
        builder.rawText(damageRolledChatLog + "\n" + String.join("\n", targetDamageAppliedChatLogs));

        parseDamageRollChatLog(damageRolledChatLog, builder);
        var damgeTypes = parseDamageTypes(damageRolledChatLog);
        builder.targets(targetDamageAppliedChatLogs.stream()
                .map(targetDamageAppliedChatLog -> parseDamageAppliedChatLog(targetDamageAppliedChatLog, damgeTypes))
                .toList());

        return builder.build();
    }

    private static void parseDamageRollChatLog(String damageRollChatLog, ChatLogEntry.ChatLogEntryBuilder builder) {
        var indexAttacker = damageRollChatLog.indexOf(":");
        builder.mainActor(damageRollChatLog.substring(0, indexAttacker).trim());

        var indexDiceRollResult = damageRollChatLog.lastIndexOf("[");
        var diceRollExpression = damageRollChatLog.substring(indexDiceRollResult + 1, damageRollChatLog.length() - 1);
        var diceRollResult = ParserUtils.parseDiceRollResult(diceRollExpression)
                .modifiers(List.of())
                .build();
        builder.diceRollResult(diceRollResult);
    }

    private static ChatLogEntry.ActionTarget parseDamageAppliedChatLog(String targetDamageAppliedChatLog, Set<ChatLogEntry.DamageType> damageTypes) {
        var targetNameStartIndex = targetDamageAppliedChatLog.indexOf("[to ") + 4;
        var targetNameEndIndex = targetDamageAppliedChatLog.indexOf("]", targetNameStartIndex);
        var targetName = targetDamageAppliedChatLog.substring(targetNameStartIndex, targetNameEndIndex).trim();

        var matcher = PATTERN_DAMAGE_DONE.matcher(targetDamageAppliedChatLog);
        int damageDone = 0;
        if (matcher.find()) {
            var damageDoneString = matcher.group(0);
            damageDone = Integer.parseInt(damageDoneString.substring(1, damageDoneString.length() - 1));
        }

        int overKillDamage = 0;
        ChatLogEntry.ActionResult actionResult = ChatLogEntry.ActionResult.DAMAGE;
        if (targetDamageAppliedChatLog.contains("[STATUS: Dying]")) {
            actionResult = ChatLogEntry.ActionResult.KILLING_BLOW;
            if (targetDamageAppliedChatLog.contains("[DAMAGE EXCEEDS HIT POINTS BY ")) {
                var overKillStartIndex = targetDamageAppliedChatLog.indexOf("[DAMAGE EXCEEDS HIT POINTS BY ") + "[DAMAGE EXCEEDS HIT POINTS BY ".length();
                var overKillEndIndex = targetDamageAppliedChatLog.indexOf(']', overKillStartIndex);
                var overKillDamageString = targetDamageAppliedChatLog.substring(overKillStartIndex, overKillEndIndex);
                overKillDamage = Integer.parseInt(overKillDamageString);
            }
        }
        return ChatLogEntry.ActionTarget.builder()
                .targetName(targetName)
                .damage(new ChatLogEntry.Damage(damageTypes, damageDone, overKillDamage))
                .actionResult(actionResult)
                .build();
    }

    private static Set<ChatLogEntry.DamageType> parseDamageTypes(String chatLog) {
        var damageTypes = new HashSet<ChatLogEntry.DamageType>();
        var damageTypeIndexStart = chatLog.indexOf("[TYPE: ");
        while (damageTypeIndexStart != -1) {
            var damageTypeIndexEnd = chatLog.indexOf('(', damageTypeIndexStart) - 1;
            var damageTypeString = chatLog.substring(damageTypeIndexStart + 7, damageTypeIndexEnd);
            if (damageTypeString.contains(",")) {
                Arrays.stream(damageTypeString.split(","))
                        .map(DamageParser::parseDamageType)
                        .filter(type -> !NO_TYPE.equals(type))
                        .forEach(damageTypes::add);
            } else {
                var damageType = parseDamageType(damageTypeString);
                if (!NO_TYPE.equals(damageType)) {
                    damageTypes.add(parseDamageType(chatLog.substring(damageTypeIndexStart + 7, damageTypeIndexEnd)));
                }
            }
            damageTypeIndexStart = chatLog.indexOf("[TYPE: ", damageTypeIndexStart + 1);
        }
        return damageTypes;
    }

    private static ChatLogEntry.DamageType parseDamageType(String damageTypeString) {
        switch (damageTypeString.toLowerCase()) {
            case "bludgeoning" -> {
                return BLUDGEONING;
            }
            case "piercing" -> {
                return PIERCING;
            }
            case "slashing" -> {
                return SLASHING;
            }
            case "fire" -> {
                return FIRE;
            }
            case "cold" -> {
                return COLD;
            }
            case "acid" -> {
                return ACID;
            }
            case "thunder" -> {
                return THUNDER;
            }
            case "radiant" -> {
                return RADIANT;
            }
            case "lightning" -> {
                return LIGHTNING;
            }
            case "necrotic" -> {
                return NECROTIC;
            }
            case "poison" -> {
                return POISON;
            }
            case "psychic" -> {
                return PSYCHIC;
            }
            default -> {
                return NO_TYPE;
            }
        }
    }
}
