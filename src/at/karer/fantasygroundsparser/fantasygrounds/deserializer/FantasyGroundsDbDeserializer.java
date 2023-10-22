package at.karer.fantasygroundsparser.fantasygrounds.deserializer;

import at.karer.fantasygroundsparser.commandline.ErrorMessages;
import at.karer.fantasygroundsparser.configuration.JacksonConfig;
import at.karer.fantasygroundsparser.fantasygrounds.model.CharacterSheet;
import at.karer.fantasygroundsparser.fantasygrounds.model.FantasyGroundsDB;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static at.karer.fantasygroundsparser.fantasygrounds.FantasyGroundsConstants.*;

public class FantasyGroundsDbDeserializer {

    public static FantasyGroundsDB deserializeDB(Path campaignFolder) {
        var node = readFile(campaignFolder);
        var db = new FantasyGroundsDB();

        if (!node.has(XML_CHARACTER_SHEETS_PARENT)) {
            ErrorMessages.outputError(String.format(ErrorMessages.XML_FIELD_NOT_FOUND, XML_CHARACTER_SHEETS_PARENT));
            System.exit(1);
        }
        db.setCharacterSheets(deserializeCharacterSheets(node.findValue(XML_CHARACTER_SHEETS_PARENT)));

        return db;
    }

    private static JsonNode readFile(Path campaignFolder) {
        try {
            var content = Files.readAllBytes(campaignFolder.resolve(FILE_DB));
            var xmlMapper = JacksonConfig.getXmlMapperInstance();
            return xmlMapper.readTree(content);
        } catch (IOException e) {
            ErrorMessages.outputError(String.format(ErrorMessages.FILE_ACCESS_ERROR, FILE_DB));
            System.exit(1);
            return null;
        }
    }

    private static List<CharacterSheet> deserializeCharacterSheets(JsonNode node) {
        var xmlMapper = JacksonConfig.getXmlMapperInstance();
        var characterSheets = new ArrayList<CharacterSheet>(node.size());
        try {
            for (var it = node.elements(); it.hasNext(); ) {
                var charSheetNode = it.next();
                var charSheet = xmlMapper.treeToValue(charSheetNode, CharacterSheet.class);
                characterSheets.add(charSheet);
            }
        } catch (JsonProcessingException e) {
            ErrorMessages.outputError(String.format(ErrorMessages.GENERAL_DESERIALIZATION, XML_CHARACTER_SHEETS_PARENT, e.getMessage()));
            System.exit(1);
        }
        return characterSheets;
    }
}
