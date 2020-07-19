package fr.raksrinana.rsndiscord;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import picocli.CommandLine;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contains the elements passed through the CLI.
 */
@SuppressWarnings("FieldMayBeFinal")
@NoArgsConstructor
@Getter
@CommandLine.Command(name = "rsndiscord", mixinStandardHelpOptions = true)
public class CLIParameters{
	@CommandLine.Option(names = {
			"-s",
			"--settings"
	},
			description = "The guild settings folder to use")
	@NonNull
	private Path settingsPath = Paths.get("config/settings");
	@CommandLine.Option(names = {
			"-c",
			"--config"
	},
			description = "The configuration file")
	@NonNull
	private Path configurationFile = Paths.get("config/config.properties");
}
