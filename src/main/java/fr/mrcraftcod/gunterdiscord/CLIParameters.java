package fr.mrcraftcod.gunterdiscord;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import java.io.File;

public class CLIParameters{
	@Parameter(names = {
			"-s",
			"--settings"
	}, description = "The settings file to use", converter = FileConverter.class)
	private File settingsFile = new File("settings.json");
	
	@Parameter(names = {
			"-c",
			"--config"
	}, description = "The configuration file", converter = FileConverter.class)
	private File configurationFile = null;
	
	public CLIParameters(){
	}
	
	public File getSettingsFile(){
		return this.settingsFile.getAbsoluteFile();
	}
	
	public File getConfigurationFile(){
		return configurationFile;
	}
}
