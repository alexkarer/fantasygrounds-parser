package at.karer.fantasygroundsparser.fantasygrounds.model;

import java.util.List;

public class FantasyGroundsDB {
    private List<CharacterSheet> characterSheets;

    public List<CharacterSheet> getCharacterSheets() {
        return characterSheets;
    }

    public void setCharacterSheets(List<CharacterSheet> characterSheets) {
        this.characterSheets = characterSheets;
    }
}