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
                        \t\tCritical misses made: %d (%.2f%%)
                        """;
    private final static String ATTACK_ROLLS_RECEIVED = """
                        \t\tTotal attacks: %d
                        \t\tAttacks taken: %d (%.2f%%)
                        \t\tAttacks avoided: %d (%.2f%%)
                        \t\tCritical hits taken: %d (%.2f%%)
                        \t\tCritical misses avoided: %d (%.2f%%)
                        """;

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

        System.out.println("\nDAMAGE:");
        printDamageDone(characterStats);
        printDamageReceived(characterStats);

        System.out.println("\nHEALING:");
        printHealingStats(characterStats);

        System.out.println("\nSAVING THROWS:");
        printSavingThrowStats(characterStats);

        System.out.println("\nDEATH SAVING THROWS:");
        printDeathSavingThrowsMade(characterStats.deathSavingThrowsMade());
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
                attackRolls.attacksHit(), attackRolls.percentageHit(),
                attackRolls.attacksMissed(), attackRolls.percentageMissed(),
                attackRolls.criticalHits(), attackRolls.percentageCriticalHit(),
                attackRolls.criticalMisses(), attackRolls.percentageCriticalMissed());
    }

    private static void printDamageDone(CampaignStatistics.CharacterStats characterStats) {
        System.out.printf("""
                        \tDAMAGE DONE:"
                        \t\tTotal damage done: %d
                        \t\tTotal overkill damage done: %d
                        \t\tDamage breakdown per type:
                        """, characterStats.totalDamageDone(), characterStats.totalOverkillDamageDone());
        characterStats.damageDonePerType().entrySet().stream()
                .filter(damage -> damage.getValue().damageDone() != 0)
                .forEach(damage -> System.out.printf("\t\t\t%s: %d\n", damage.getKey().name(), damage.getValue().damageDone()));
    }

    private static void printDamageReceived(CampaignStatistics.CharacterStats characterStats) {
        System.out.printf("""
                \tDAMAGE RECEIVED:
                \t\tTotal damage received: %d
                \t\tTotal damage resisted: %d
                \t\tDamage received breakdown per type:
                """, characterStats.totalDamageReceived(), characterStats.totalDamageResisted());
        characterStats.damageReceivedPerType().entrySet().stream()
                .filter(damage -> damage.getValue().damageDone() != 0)
                .forEach(damage -> System.out.printf("\t\t\t%s: %d\n", damage.getKey().name(), damage.getValue().damageDone()));
    }

    private static void printHealingStats(CampaignStatistics.CharacterStats characterStats) {
        System.out.printf("""
                \tTotal healing received %d
                \tTotal healing done: %d
                \tHealing done breakdown per healed creature:
                """, characterStats.healingReceived(), characterStats.totalHealingDone());

        characterStats.healingDone()
                .forEach(healing -> System.out.printf("\t\t%s: %d\n", healing.target(), healing.healingDone()));
    }

    private static void printSavingThrowStats(CampaignStatistics.CharacterStats characterStats) {
        System.out.printf("""
                \tSaving Throws made: %d
                \tSaving Throws succeeded: %d (%.2f%%)
                \tSaving Throws failed: %d (%.2f%%)
                \tSaving Throw breakdown per Ability:
                """,
                characterStats.totalSavesMade(),
                characterStats.totalSavesSucceded(), characterStats.percentageSavesSucceeded(),
                characterStats.totalSavesFailed(), characterStats.percentageSavesFailed());

        characterStats.savingThrowsMade().forEach((key, value) ->
                System.out.printf("""
                        \t\t%s:
                        \t\t\ttotal made: %d
                        \t\t\tSucceded: %d (%.2f%%)
                        \t\t\tFailed: %d (%.2f%%)
                        """,
                        key,
                        value.totalMade(),
                        value.succeeded(), value.percentageSucceded(),
                        value.failed(), value.percentageFailed()));
    }

    private static void printDeathSavingThrowsMade(CampaignStatistics.CharacterStats.DeathSavingThrows deathSavingThrows) {
        System.out.printf("""
                \tDeath Saving Throws made: %d
                \tDeath Saving Throws succeeded: %d (%.2f%%)
                \tDeath Saving Throws failed: %d (%.2f%%)
                \tDeath Saving Throws critically succeded: %d (%.2f%%)
                \tDeath Saving Throws critically failed: %d (%.2f%%)
                """,
                deathSavingThrows.totalMade(),
                deathSavingThrows.succeded(), deathSavingThrows.percentageSucceded(),
                deathSavingThrows.failed(), deathSavingThrows.percentageFailed(),
                deathSavingThrows.criticalSuccesses(), deathSavingThrows.percentageCriticallySucceded(),
                deathSavingThrows.criticalFails(), deathSavingThrows.percentageCriticallyFailed());
    }
}
