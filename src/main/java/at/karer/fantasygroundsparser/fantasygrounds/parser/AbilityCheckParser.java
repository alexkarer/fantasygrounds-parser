package at.karer.fantasygroundsparser.fantasygrounds.parser;

import at.karer.fantasygroundsparser.fantasygrounds.model.ChatLogEntry;

public class AbilityCheckParser {

    /**
     * Transforms raw chatlog text about an ability check into structured data
     * There are 3 main types of ability checks: Raw Ability Check, Skill Check, Initiative Roll
     * Raw Ability Check Format: Name ": [CHECK] " AbilityName [" [ADV]"] [" [DIS]"] [" [DROPPED " Number "]"] " [" DiceExpression "]"
     * Skill Check Format: Name ": [SKILL] " SkillName [" [PROF]"] [" [ADV]"] [" [DIS]"] [" [DROPPED " Number "]"] " [" DiceExpression "]"
     * Initiative Roll Format: Name ": [INIT]" [" [ADV]"] [" [DIS]"] [" [DROPPED " Number "]"] " [" DiceExpression "]"
     * @param abilityCheckChatLog the raw chatlog information about the ability check
     * @return ChatLogEntry with all information fit in for an ability check
     */
    static ChatLogEntry createAbilityCheckEntry(String abilityCheckChatLog) {
        var builder = ChatLogEntry.builder();
        builder.type(ChatLogEntry.ChatLogEntryType.CHECK);
        builder.rawChatlogs(1);
        builder.rawText(abilityCheckChatLog);

        builder.mainActor(ParserUtils.getMainActorName(abilityCheckChatLog));

        var diceRollResultBuilder = ParserUtils.getDiceRollResult(abilityCheckChatLog);
        var diceRollResult = diceRollResultBuilder
                .modifiers(ParserUtils.addModifiers(abilityCheckChatLog))
                .build();
        builder.diceRollResult(diceRollResult);

        if (abilityCheckChatLog.contains("[CHECK]") || abilityCheckChatLog.contains("[SKILL]")) {
            builder.abilityName(ParserUtils.getAbilityName(abilityCheckChatLog));
        } else if (abilityCheckChatLog.contains("[INIT]")) {
            builder.abilityName("Initiative");
        }

        return builder.build();
    }
}
