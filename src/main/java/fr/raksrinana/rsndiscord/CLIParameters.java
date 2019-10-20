package fr.raksrinana.rsndiscord;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

@SuppressWarnings("FieldMayBeFinal")
class CLIParameters{
	@Parameter(names = {
			"-s",
			"--jackson "
	}, description = "The settings file to use", converter = FileConverter.class)
	private File settingsFile = new File("settings.json");
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
	File getSettingsFile(){
		return this.settingsFile.getAbsoluteFile();
	}
}
