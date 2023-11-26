package at.karer.fantasygroundsparser.statsgeneration.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public record CampaignStatistics (
    List<CharacterStats> characterStatsList,
    CampaignHighlights campaignHighlights

) {
    public record CharacterStats (
        CharacterInfo characterInfo,
        AttackRolls attackRollsMade,
        AttackRolls attackRollsReceived,
        Map<Damage.DamageType, Damage> damageDonePerType,
        Map<Damage.DamageType, Damage> damageReceivedPerType,
        List<Healing> healingDone,
        int healingReceived,
        Map<String, SavingThrow> savingThrowsMade,
        DeathSavingThrows deathSavingThrowsMade
    ) {
        public int totalDamageDone() {
            return sumIntPropertyFromList(damageDonePerType.values(), Damage::damageDone);
        }

        public int totalOverkillDamageDone() {
            return sumIntPropertyFromList(damageDonePerType.values(), Damage::overkillDamage);
        }

        public int totalDamageReceived() {
            return sumIntPropertyFromList(damageReceivedPerType.values(), Damage::damageDone);
        }

        public int totalDamageResisted() {
            return sumIntPropertyFromList(damageReceivedPerType.values(), Damage::damageResisted);
        }

        public int totalHealingDone() {
            return sumIntPropertyFromList(healingDone, Healing::healingDone);
        }

        public int totalSavesSucceded() {
            return sumIntPropertyFromList(savingThrowsMade.values(), SavingThrow::succeeded);
        }

        public int totalSavesFailed() {
            return sumIntPropertyFromList(savingThrowsMade.values(), SavingThrow::failed);
        }

        public int totalSavesMade() {
            return sumIntPropertyFromList(savingThrowsMade.values(), SavingThrow::totalMade);
        }

        public double percentageSavesSucceeded() {
            return ((double) totalSavesSucceded() / totalSavesMade()) * 100;
        }

        public double percentageSavesFailed() {
            return ((double) totalSavesFailed() / totalSavesMade()) * 100;
        }

        private <T> int sumIntPropertyFromList(Collection<T> collection, Function<T, Integer> propertyRetriever) {
            if (collection == null) {
                return 0;
            }
            return collection.stream()
                    .mapToInt(propertyRetriever::apply)
                    .sum();
        }
        public record CharacterInfo (
            String name,
            int level,
            String race,
            List<CharacterClass> classes
        ) {
            public record CharacterClass (
                String name,
                int level
            ) { }
        }

        public record AttackRolls (
            int attacksHit,
            int attacksMissed,
            int criticalHits,
            int criticalMisses
        ) {
            public int attacksMade() {
                return criticalHits + criticalMisses + attacksHit + attacksMissed;
            }
            public double percentageHit() {
                return ((double) attacksHit() / attacksMade()) * 100;
            }

            public double percentageMissed() {
                return ((double) attacksMissed() / attacksMade()) * 100;
            }

            public double percentageCriticalHit() {
                return ((double) criticalHits() / attacksMade()) * 100;
            }

            public double percentageCriticalMissed() {
                return ((double) criticalMisses() / attacksMade()) * 100;
            }
        }

        public record Damage (
            int damageDone,
            int damageResisted,
            int overkillDamage
        ) {
            public enum DamageType {
                NO_TYPE,
                BLUDGEONING,
                PIERCING,
                SLASHING,
                FIRE,
                LIGHTNING,
                POISON,
                ACID,
                THUNDER,
                COLD,
                RADIANT,
                NECROTIC,
                PSYCHIC
            }
        }

        public record Healing (
            String target,
            int healingDone
        ) {}

        public record SavingThrow (
            int succeeded,
            int failed
        ) {
            public int totalMade() {
                return succeeded + failed;
            }

            public double percentageSucceded() {
                return ((double) succeeded / totalMade()) * 100;
            }

            public double percentageFailed() {
                return ((double) failed / totalMade()) * 100;
            }
        }

        public record DeathSavingThrows (
            int succeded,
            int failed,
            int criticalSuccesses,
            int criticalFails
        ) {
            public int totalMade() {
                return criticalSuccesses + criticalFails + succeded + failed;
            }

            public double percentageSucceded() {
                return ((double) succeded / totalMade()) * 100;
            }

            public double percentageFailed() {
                return ((double) failed / totalMade()) * 100;
            }

            public double percentageCriticallySucceded() {
                return ((double) criticalSuccesses / totalMade()) * 100;
            }

            public double percentageCriticallyFailed() {
                return ((double) criticalFails / totalMade()) * 100;
            }
        }
    }

    public record CampaignHighlights (

    ) {}
}
