package at.karer.fantasygroundsparser.fantasygrounds.model;

import lombok.Builder;

import java.util.List;

@Builder
public record CharacterSheet (
        String name,
        String race,
        Integer level,
        List<CharacterClass> classes
) {
    public record CharacterClass (
            String name,
            Integer level
    ) {}
}
