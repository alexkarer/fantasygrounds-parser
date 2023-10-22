package at.karer.fantasygroundsparser.commandline.converter;

import at.karer.fantasygroundsparser.commandline.ErrorMessages;
import at.karer.fantasygroundsparser.fantasygrounds.FantasyGroundsConstants;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

import java.nio.file.*;

public class PathConverter implements IStringConverter<Path> {
    @Override
    public Path convert(String s) {
        Path path;
        try {
            path = Paths.get(s);
            Files.isDirectory(path);
            if (!Files.exists(path.resolve(FantasyGroundsConstants.FILE_DB))) {
                throw new ParameterException(String.format(ErrorMessages.INVALID_FG_DIRECTORY, FantasyGroundsConstants.FILE_DB, s));
            }
            if (!Files.exists(path.resolve(FantasyGroundsConstants.FILE_CHATLOG))) {
                throw new ParameterException(String.format(ErrorMessages.INVALID_FG_DIRECTORY, FantasyGroundsConstants.FILE_CHATLOG, s));
            }
        } catch (InvalidPathException e) {
            throw new ParameterException("Invalid File Path specified: " + e.getMessage(), e);
        }
        return path;
    }
}
