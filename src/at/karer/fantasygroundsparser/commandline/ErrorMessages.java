package at.karer.fantasygroundsparser.commandline;

public class ErrorMessages {

    // FILE ERRORS
    public static String INVALID_FG_DIRECTORY = "<%s> File does not exists, are you sure <%s> is a correct Fantasygrounds campaign directory?";
    public static String FILE_ACCESS_ERROR = "Error accessing <%s>";


    // DESERIALIZATION ERRORS
    public static String GENERAL_DESERIALIZATION = "Error deserializing Fantasygrounds File for field <%s> with error messsage <%s>";
    public static String XML_FIELD_NOT_FOUND = "Error deserializing Fantasygrounds File field <%s> not found";

    public static void outputError(String errorMessage) {
        System.err.printf("ERROR: %s%n", errorMessage);
    }
}
