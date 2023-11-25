package at.karer.fantasygroundsparser.statsgeneration.model;

import java.util.List;

public record CampaignStatistics (
    List<CharacterStats> characterStatsList,
    CampaignHighlights campaignHighlights

) {
    public record CharacterStats (
        CharacterInfo characterInfo,
        AttackRolls attackRollsMade,
        AttackRolls attackRollsReceived,
        List<Damage> damageDone,
        List<Damage> damageReceived
    ) {
        public int totalDamageDone() {
            return totalDamage(damageDone);
        }

        public int totalOverkillDamageDone() {
            if (damageDone == null) {
                return 0;
            }
            return damageDone.stream()
                    .mapToInt(Damage::overkillDamage)
                    .sum();
        }

        public int totalDamageReceived() {
            return totalDamage(damageReceived);
        }

        public int totalDamageResisted() {
            if (damageReceived == null) {
                return 0;
            }
            return damageReceived.stream()
                    .mapToInt(Damage::damageResisted)
                    .sum();
        }

        private int totalDamage(List<Damage> damage) {
            if (damage == null) {
                return 0;
            }
            return damage.stream()
                    .mapToInt(Damage::damageDone)
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
            int attacksMade,
            int attackHit,
            int attacksMissed,
            int criticalHits,
            int criticalMisses
        ) {
            public double percentageHit() {
                return ((double) attackHit() / attacksMade()) * 100;
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
            DamageType type,
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
    }

    public record CampaignHighlights (

    ) {}
}
