package at.karer.fantasygroundsparser.statsgeneration.model;

import java.util.List;

public record CampaignStatistics (
    List<CharacterStats> characterStatsList,
    CampaignHighlights campaignHighlights

) {
    public record CharacterStats (
        CharacterInfo characterInfo,
        AttackRolls attackRollsMade,
        AttackRolls attackRollsReceived
    ) {
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
        ) {}
    }

    public record CampaignHighlights (

    ) {}
}
