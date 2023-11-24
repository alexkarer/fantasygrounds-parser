package at.karer.fantasygroundsparser.fantasygrounds.deserializer;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class FantasyGroundsDbDeserializerTest {

    @Test
    void testDbDeserializer() {
        var testDataPath = Path.of("src/test/resources/testdata");

        var db = FantasyGroundsDbDeserializer.deserializeDB(testDataPath);

        assertThat(db).isNotNull();
        assertThat(db.characterSheets()).hasSize(2);

        var charFighter = db.characterSheets().get("TestCharacter Human Fighter");
        assertThat(charFighter.name()).isEqualTo("TestCharacter Human Fighter");
        assertThat(charFighter.level()).isEqualTo(1);
        assertThat(charFighter.race()).isEqualTo("Human");

        var charPriest = db.characterSheets().get("TestCharacter Dwarven Priest");
        assertThat(charPriest.name()).isEqualTo("TestCharacter Dwarven Priest");
        assertThat(charPriest.level()).isEqualTo(1);
        assertThat(charPriest.race()).isEqualTo("Hill Dwarf");
    }
}