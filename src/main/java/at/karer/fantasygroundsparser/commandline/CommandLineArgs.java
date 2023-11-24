package at.karer.fantasygroundsparser.commandline;

import at.karer.fantasygroundsparser.commandline.converter.PathConverter;
import com.beust.jcommander.Parameter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.nio.file.Path;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommandLineArgs {

    @Parameter(required = true,
               names = "-campaign",
               description = "Path to your campaign folder, on Windows you can find it at %appdata%\\SmiteWorks\\Fantasy Grounds\\campaigns",
               converter = PathConverter.class)
    private Path campaignFolder;
}
