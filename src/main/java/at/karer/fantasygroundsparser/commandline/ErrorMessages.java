package at.karer.fantasygroundsparser.commandline;

public class ErrorMessages {

    // FILE ERRORS
    public static String INVALID_FG_DIRECTORY = "%s File does not exists, are you sure %s is a correct Fantasygrounds campaign directory?";
    public static String FILE_ACCESS_ERROR = "Error accessing file {}";


    // DESERIALIZATION ERRORS
    public static String GENERAL_DESERIALIZATION = "Error deserializing Fantasygrounds File for field{}} with error messsage {}";
    public static String XML_FIELD_NOT_FOUND = "Error deserializing Fantasygrounds File field {} not found";


    // PARSING ERRORS
    public static String GENERAL_PARSING_ERROR = "Error parsing chatlog: index: {} rawtext: {}, with error message: {}";
    public static String EXPECTED_TEXT_MISSING = "Error parsing chatlog: expected chatlog: {} to contain one of: {}";

}
