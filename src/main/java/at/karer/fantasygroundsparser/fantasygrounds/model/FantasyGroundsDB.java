package at.karer.fantasygroundsparser.fantasygrounds.model;

import lombok.Builder;

import java.util.Map;

@Builder
public record FantasyGroundsDB (
    Map<String, CharacterSheet> characterSheets
) { }