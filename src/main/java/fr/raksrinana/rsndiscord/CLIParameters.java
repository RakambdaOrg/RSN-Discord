package fr.raksrinana.rsndiscord;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import com.beust.jcommander.converters.PathConverter;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("FieldMayBeFinal")
public class CLIParameters{
	@Parameter(names = {
			"-s",
			"--settings "
	}, description = "The guild settings folder to use", converter = PathConverter.class)
	private Path settingsFolder = Paths.get("settings");
	@Parameter(names = {
			"-c",
			"--config"
	}, description = "The configuration file", converter = FileConverter.class, required = true)
	private File configurationFile = null;
	
	CLIParameters(){
	}
	
	@Nullable
	File getConfigurationFile(){
		return this.configurationFile;
	}
	
	@Nonnull
	public Path getSettingsFolder(){
		return this.settingsFolder;
	}
}
