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

        System.out.println("DAMAGE:");
        printDamageDone(characterStats);
        printDamageReceived(characterStats);
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
                attackRolls.attackHit(), attackRolls.percentageHit(),
                attackRolls.attacksMissed(), attackRolls.percentageMissed(),
                attackRolls.criticalHits(), attackRolls.percentageCriticalHit(),
                attackRolls.criticalMisses(), attackRolls.percentageCriticalMissed());
    }

    private static void printDamageDone(CampaignStatistics.CharacterStats characterStats) {
        System.out.println("\tDAMAGE DONE:");
        System.out.printf("\t\tTotal damage done: %d\n", characterStats.totalDamageDone());
        System.out.printf("\t\tTotal overkill damage done: %d\n", characterStats.totalOverkillDamageDone());
        System.out.println("\t\tDamage breakdown per type:");
        characterStats.damageDone().stream()
                .filter(damage -> damage.damageDone() != 0)
                .forEach(damage -> System.out.printf("\t\t\t%s: %d\n", damage.type().name(), damage.damageDone()));
    }

    private static void printDamageReceived(CampaignStatistics.CharacterStats characterStats) {
        System.out.println("\tDAMAGE RECEIVED:");
        System.out.printf("\t\tTotal damage received: %d\n", characterStats.totalDamageReceived());
        System.out.printf("\t\tTotal damage resisted: %d\n", characterStats.totalDamageResisted());
        System.out.println("\t\tDamage received breakdown per type:");
        characterStats.damageDone().stream()
                .filter(damage -> damage.damageDone() != 0)
                .forEach(damage -> System.out.printf("\t\t\t%s: %d\n", damage.type().name(), damage.damageDone()));
    }
}
