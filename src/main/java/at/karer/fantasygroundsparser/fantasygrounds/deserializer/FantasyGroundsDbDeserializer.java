package at.karer.fantasygroundsparser.fantasygrounds.deserializer;

import at.karer.fantasygroundsparser.commandline.ErrorMessages;
import at.karer.fantasygroundsparser.configuration.JacksonConfig;
import at.karer.fantasygroundsparser.fantasygrounds.model.CharacterSheet;
import at.karer.fantasygroundsparser.fantasygrounds.model.FantasyGroundsDB;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static at.karer.fantasygroundsparser.fantasygrounds.FantasyGroundsConstants.*;

@Slf4j
public class FantasyGroundsDbDeserializer {

    public static FantasyGroundsDB deserializeDB(Path campaignFolder) {
        var node = readFile(campaignFolder);
        var dbBuilder = FantasyGroundsDB.builder();

        if (!node.has(XML_CHARSHEET_PARENT)) {
            log.error(ErrorMessages.XML_FIELD_NOT_FOUND, XML_CHARSHEET_PARENT);
            System.exit(1);
        }
        dbBuilder.characterSheets(deserializeCharacterSheets(node.findValue(XML_CHARSHEET_PARENT)));

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
        var characterSheets = new HashMap<String, CharacterSheet>(node.size());
        for (var it = node.elements(); it.hasNext(); ) {
            var charSheetNode = it.next();
            var charSheet = CharacterSheet.builder()
                    .name(charSheetNode.get(XML_CHARSHEET_NAME).get("").asText())
                    .level(charSheetNode.get(XML_CHARSHEET_LEVEL).get("").asInt())
                    .race(charSheetNode.get(XML_CHARSHEET_RACE).get("").asText())
                    .classes(deserializeClasses(charSheetNode.get(XML_CHARSHEET_CLASSES)))
                    .build();
            characterSheets.put(charSheet.name(), charSheet);
        }
        return characterSheets;
    }

    private static List<CharacterSheet.CharacterClass> deserializeClasses(JsonNode node) {
        if (node.isNull()) {
            log.warn(ErrorMessages.XML_FIELD_NOT_FOUND, XML_CHARSHEET_CLASSES);
            return List.of();
        }
        var classes = new ArrayList<CharacterSheet.CharacterClass>();
        for (var it = node.elements(); it.hasNext(); ) {
            var charClass = it.next();
            if (charClass.has(XML_CLASS_LEVEL)) {
                classes.add(new CharacterSheet.CharacterClass(
                        charClass.get(XML_CLASS_NAME).get("").asText(),
                        charClass.get(XML_CLASS_LEVEL).get("").asInt()
                ));
            }
        }
        return classes;
    }
}
