package fr.raksrinana.rsndiscord;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.PathConverter;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("FieldMayBeFinal")
public class CLIParameters{
	@Parameter(names = {
			"-s",
			"--settings "
	}, description = "The guild settings folder to use", converter = PathConverter.class)
	private Path settingsFolder = Paths.get("config/settings");
	@Parameter(names = {
			"-c",
			"--config"
	}, description = "The configuration file", converter = PathConverter.class)
	private Path configurationFile = Paths.get("config/config.properties");
	
	CLIParameters(){
	}
	
	@Nullable
	Path getConfigurationFile(){
		return this.configurationFile;
	}
	
	@Nonnull
	public Path getSettingsFolder(){
		return this.settingsFolder;
	}
}
