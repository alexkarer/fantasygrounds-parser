package at.karer.fantasygroundsparser.commandline.printer;

import at.karer.fantasygroundsparser.statsgeneration.model.CampaignStatistics;

import java.util.List;
import java.util.stream.Collectors;

public class StatsPrinter {

    private final static String ATTACK_ROLLS_MADE = """
                        \t\tTotal attacks: %d
                        \t\tAttacks hit: %d (%.2f%%)
                        \t\tAttacks missed: %d (%.2f%%)
                        \t\tCritical hits made: %d (%.2f%%)
                        \t\tCritical misses made: %d (%.2f%%)%n""";
    private final static String ATTACK_ROLLS_RECEIVED = """
                        \t\tTotal attacks: %d
                        \t\tAttacks taken: %d (%.2f%%)
                        \t\tAttacks avoided: %d (%.2f%%)
                        \t\tCritical hits taken: %d (%.2f%%)
                        \t\tCritical misses avoided: %d (%.2f%%)%n""";

    public static void printStatistics(CampaignStatistics stats) {
        stats.characterStatsList().forEach(StatsPrinter::printCharacterStats);
    }

    private static void printCharacterStats(CampaignStatistics.CharacterStats characterStats) {
        printCharacterInfo(characterStats.characterInfo());
        System.out.println("ATTACK ROLLS:");
        System.out.println("\tATTACKS MADE:");
        printAttackRolls(characterStats.attackRollsMade(), ATTACK_ROLLS_MADE);
        System.out.println("\tATTACKS RECEIVED:");
        printAttackRolls(characterStats.attackRollsReceived(), ATTACK_ROLLS_RECEIVED);
    }

    private static void printCharacterInfo(CampaignStatistics.CharacterStats.CharacterInfo characterInfo) {
        System.out.println("*******************************************************************");
        System.out.printf("%s, Level: %d, %s, %s\n", characterInfo.name(), characterInfo.level(), prettyStringClasses(characterInfo.classes()), characterInfo.race());
        System.out.println("*******************************************************************");
    }

    private static String prettyStringClasses(List<CampaignStatistics.CharacterStats.CharacterInfo.CharacterClass> classes) {
        if (classes.size() == 1) {
            return classes.get(0).name();
        }
        return classes.stream().map(c -> String.format("%s: %d", c.name(), c.level())).collect(Collectors.joining(" "));
    }

    private static void printAttackRolls(CampaignStatistics.CharacterStats.AttackRolls attackRolls, String AttackRollPrintText) {
        System.out.printf(AttackRollPrintText,
                attackRolls.attacksMade(),
                attackRolls.attackHit(), ((double)attackRolls.attackHit() / attackRolls.attacksMade()) * 100,
                attackRolls.attacksMissed(), ((double)attackRolls.attacksMissed() / attackRolls.attacksMade()) * 100,
                attackRolls.criticalHits(), ((double)attackRolls.criticalHits() / attackRolls.attacksMade()) * 100,
                attackRolls.criticalMisses(), ((double)attackRolls.criticalMisses() / attackRolls.attacksMade()) * 100);
    }
}
