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
        assertThat(db.getCharacterSheets()).hasSize(2);

        assertThat(db.getCharacterSheets().get(0).name()).isEqualTo("TestCharacter Human Fighter");
        assertThat(db.getCharacterSheets().get(0).level()).isEqualTo(1);
        assertThat(db.getCharacterSheets().get(0).race()).isEqualTo("Human");

        assertThat(db.getCharacterSheets().get(1).name()).isEqualTo("TestCharacter Dwarven Priest");
        assertThat(db.getCharacterSheets().get(1).level()).isEqualTo(1);
        assertThat(db.getCharacterSheets().get(1).race()).isEqualTo("Hill Dwarf");
    }
}