package fr.raksrinana.rsndiscord;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contains the elements passed through the CLI.
 */
@NoArgsConstructor
@Getter
@Command(name = "rsndiscord", mixinStandardHelpOptions = true)
public class CLIParameters{
	@Option(names = {
			"-s",
			"--settings"
	},
			description = "The guild settings folder to use")
	@NotNull
	private Path settingsPath = Paths.get("config/settings");
	@Option(names = {
			"-c",
			"--config"
	},
			description = "The configuration file")
	@NotNull
	private Path configurationFile = Paths.get("config/config.properties");
}
