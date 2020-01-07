package fr.raksrinana.rsndiscord;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.PathConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contains the elements passed through the CLI.
 */
@NoArgsConstructor
@Getter
public class CLIParameters{
	@Parameter(names = {
			"-s",
			"--settings "
	}, description = "The guild settings folder to use", converter = PathConverter.class)
	@NonNull
	private Path settingsPath = Paths.get("config/settings");
	@Parameter(names = {
			"-c",
			"--config"
	}, description = "The configuration file", converter = PathConverter.class)
	@NonNull
	private Path configurationFile = Paths.get("config/config.properties");
}
