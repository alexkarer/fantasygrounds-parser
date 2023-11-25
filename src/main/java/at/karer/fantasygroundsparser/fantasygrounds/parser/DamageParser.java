package at.karer.fantasygroundsparser.fantasygrounds.parser;

import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry.DamageType.*;

public class DamageParser {

    private static final Pattern PATTERN_DAMAGE_DONE = Pattern.compile("\\[\\d+\\]");

    /**
     * Transforms raw chatlog text about damage applied into structured data
     * Damage consists usually of 2 entries: First about the damage roll, Second information about how much damage was done
     * It is possible for damage to be rolled without a target => these will be ignored
     *  Damage Roll Format: MainActor ": " "[DAMAGE [" (R)"|" (M)"] "] " AttackName {" [EFFECTS " DiceExpression "]"} {" [TYPE: " DamageType " (" DiceExpression ")] "} " [" DiceExpression "]"
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

        var totalDamageMade = parseDamageRollChatLog(damageRolledChatLog, builder);
        var damageMade = parseDamageMade(damageRolledChatLog);
        builder.targets(targetDamageAppliedChatLogs.stream()
                .map(targetDamageAppliedChatLog -> parseDamageAppliedChatLog(targetDamageAppliedChatLog, damageMade, totalDamageMade))
                .toList());

        return builder.build();
    }

    private static int parseDamageRollChatLog(String damageRollChatLog, ChatLogEntry.ChatLogEntryBuilder builder) {
        builder.mainActor(ParserUtils.getMainActorName(damageRollChatLog));

        var diceRollResult = ParserUtils.getDiceRollResult(damageRollChatLog)
                .modifiers(List.of())
                .build();
        builder.abilityName(ParserUtils.getAbilityName(damageRollChatLog));
        builder.diceRollResult(diceRollResult);
        return diceRollResult.resultTotal();
    }

    private static List<ChatLogEntry.Damage> parseDamageMade(String chatLog) {
        var damageMade = new ArrayList<ChatLogEntry.Damage>();
        var damageTypeIndexStart = chatLog.indexOf("[TYPE: ");
        while (damageTypeIndexStart != -1) {
            var damageTypeIndexEnd = chatLog.indexOf('(', damageTypeIndexStart) - 1;
            var damageTypeString = chatLog.substring(damageTypeIndexStart + 7, damageTypeIndexEnd);

            var damageDoneIndexEnd = chatLog.indexOf(')', damageTypeIndexEnd);
            var damageDoneString = chatLog.substring(damageTypeIndexEnd + 2, damageDoneIndexEnd);

            ChatLogEntry.DamageType damageType;
            if (damageTypeString.contains(",")) {
                damageType = Arrays.stream(damageTypeString.split(","))
                        .map(DamageParser::parseDamageType)
                        .filter(type -> !NO_TYPE.equals(type))
                        .findFirst()
                        .orElse(NO_TYPE);
            } else {
                damageType = parseDamageType(damageTypeString);
            }

            var damageAmount = ParserUtils.parseDiceExpression(damageDoneString).build().resultTotal();
            damageMade.add(new ChatLogEntry.Damage(damageType, damageAmount, 0, 0));
            damageTypeIndexStart = chatLog.indexOf("[TYPE: ", damageTypeIndexStart + 1);
        }
        return damageMade;
    }

    private static ChatLogEntry.ActionTarget parseDamageAppliedChatLog(String targetDamageAppliedChatLog,
                                                                       List<ChatLogEntry.Damage> damageMade,
                                                                       int totalDamageMade) {
        var targetNameStartIndex = targetDamageAppliedChatLog.indexOf("[to ") + 4;
        var targetNameEndIndex = targetDamageAppliedChatLog.indexOf("]", targetNameStartIndex);
        var targetName = targetDamageAppliedChatLog.substring(targetNameStartIndex, targetNameEndIndex).trim();

        var matcher = PATTERN_DAMAGE_DONE.matcher(targetDamageAppliedChatLog);
        int actualDamageDone = 0;
        if (matcher.find()) {
            var damageDoneString = matcher.group(0);
            actualDamageDone = Integer.parseInt(damageDoneString.substring(1, damageDoneString.length() - 1));
        }
        var damageDoneAfterResistance = getDamageMadeAfterResistance(totalDamageMade, actualDamageDone, damageMade);

        int overKillDamage = determineOverKillDamage(targetDamageAppliedChatLog);
        var damageDoneAfterResistanceAndOverKill = getDamageMadeAfterOverkill(overKillDamage, damageDoneAfterResistance);

        var actionResult = ChatLogEntry.ActionResult.DAMAGE;
        if (targetDamageAppliedChatLog.contains("[STATUS: Dying]")) {
            actionResult = ChatLogEntry.ActionResult.KILLING_BLOW;
        }

        return ChatLogEntry.ActionTarget.builder()
                .targetName(targetName)
                .damage(damageDoneAfterResistanceAndOverKill)
                .actionResult(actionResult)
                .build();
    }

    private static List<ChatLogEntry.Damage> getDamageMadeAfterResistance(int totalDamageMade, int actualDamageDone, List<ChatLogEntry.Damage> damageMade) {
        var damageDoneAfterResistance = new ArrayList<ChatLogEntry.Damage>();
        int totalDamageResisted = totalDamageMade - actualDamageDone;
        int damageResistedPerType = totalDamageResisted / damageMade.size();
        int damageResistedPerTypeRemainder = totalDamageResisted % damageMade.size();
        for (int i = damageMade.size() - 1; i >= 0; i--) {
            int damageResisted = damageResistedPerType;
            if (damageResistedPerTypeRemainder > 0) {
                damageResisted++;
                damageResistedPerTypeRemainder--;
            }

            damageDoneAfterResistance.add(0, ChatLogEntry.Damage.builder()
                    .type(damageMade.get(i).type())
                    .damageDone(damageMade.get(i).damageDone() - damageResisted)
                    .damageResisted(damageResisted)
                    .build());
        }
        return damageDoneAfterResistance;
    }

    private static int determineOverKillDamage(String targetDamageAppliedChatLog) {
        int overKillDamage = 0;
        if (targetDamageAppliedChatLog.contains("[DAMAGE EXCEEDS HIT POINTS BY ")) {
            var overKillStartIndex = targetDamageAppliedChatLog.indexOf("[DAMAGE EXCEEDS HIT POINTS BY ") + "[DAMAGE EXCEEDS HIT POINTS BY ".length();
            var overKillEndIndex = targetDamageAppliedChatLog.indexOf(']', overKillStartIndex);
            var overKillDamageString = targetDamageAppliedChatLog.substring(overKillStartIndex, overKillEndIndex);
            overKillDamage = Integer.parseInt(overKillDamageString);
        }
        return overKillDamage;
    }

    private static List<ChatLogEntry.Damage> getDamageMadeAfterOverkill(int overKillDamage, List<ChatLogEntry.Damage> damageMade) {
        var damageDoneAfterOverKill = new ArrayList<ChatLogEntry.Damage>();
        for (int i = damageMade.size() - 1; i >= 0; i--) {
            int actualDamageDone = damageMade.get(i).damageDone();
            int overKillTemp = 0;
            if (overKillDamage != 0) {
                actualDamageDone = damageMade.get(i).damageDone() - overKillDamage;
                if (actualDamageDone < 0) {
                    actualDamageDone = 0;
                    overKillTemp = damageMade.get(i).damageDone();
                } else {
                    overKillTemp = overKillDamage;
                }
                overKillDamage -= overKillTemp;
            }
            damageDoneAfterOverKill.add(0, ChatLogEntry.Damage.builder()
                    .type(damageMade.get(i).type())
                    .damageDone(actualDamageDone)
                    .damageResisted(damageMade.get(i).damageResisted())
                    .overkillDamage(overKillTemp)
                    .build());
        }
        return damageDoneAfterOverKill;
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
