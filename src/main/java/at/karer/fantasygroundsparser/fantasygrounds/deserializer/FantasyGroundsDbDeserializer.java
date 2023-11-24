package at.karer.fantasygroundsparser.fantasygrounds.deserializer;

import at.karer.fantasygroundsparser.commandline.ErrorMessages;
import at.karer.fantasygroundsparser.configuration.JacksonConfig;
import at.karer.fantasygroundsparser.fantasygrounds.model.CharacterSheet;
import at.karer.fantasygroundsparser.fantasygrounds.model.FantasyGroundsDB;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static at.karer.fantasygroundsparser.fantasygrounds.FantasyGroundsConstants.FILE_DB;
import static at.karer.fantasygroundsparser.fantasygrounds.FantasyGroundsConstants.XML_CHARACTER_SHEETS_PARENT;

@Slf4j
public class FantasyGroundsDbDeserializer {

    public static FantasyGroundsDB deserializeDB(Path campaignFolder) {
        var node = readFile(campaignFolder);
        var dbBuilder = FantasyGroundsDB.builder();

        if (!node.has(XML_CHARACTER_SHEETS_PARENT)) {
            log.error(ErrorMessages.XML_FIELD_NOT_FOUND, XML_CHARACTER_SHEETS_PARENT);
            System.exit(1);
        }
        dbBuilder.characterSheets(deserializeCharacterSheets(node.findValue(XML_CHARACTER_SHEETS_PARENT)));

        var db = dbBuilder.build();
        log.info("Finished Deserializing {}, found {} Character Sheets", FILE_DB, db.characterSheets().size());
        return db;
    }

    private static JsonNode readFile(Path campaignFolder) {
        try {
            var content = Files.readAllBytes(campaignFolder.resolve(FILE_DB));
            var xmlMapper = JacksonConfig.getXmlMapperInstance();
            return xmlMapper.readTree(content);
        } catch (IOException e) {
            log.error(ErrorMessages.FILE_ACCESS_ERROR, FILE_DB);
            System.exit(1);
            return null;
        }
    }

    private static Map<String, CharacterSheet> deserializeCharacterSheets(JsonNode node) {
        var xmlMapper = JacksonConfig.getXmlMapperInstance();
        var characterSheets = new HashMap<String, CharacterSheet>(node.size());
        try {
            for (var it = node.elements(); it.hasNext(); ) {
                var charSheetNode = it.next();
                var charSheet = xmlMapper.treeToValue(charSheetNode, CharacterSheet.class);
                characterSheets.put(charSheet.name(), charSheet);
            }
        } catch (JsonProcessingException e) {
            log.error(ErrorMessages.GENERAL_DESERIALIZATION, XML_CHARACTER_SHEETS_PARENT, e.getMessage());
            System.exit(1);
        }
        return characterSheets;
    }
}
