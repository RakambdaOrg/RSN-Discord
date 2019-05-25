package fr.mrcraftcod.gunterdiscord;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import java.io.File;

class CLIParameters{
	@Parameter(names = {
			"-s",
			"--jackson "
	}, description = "The settings file to use", converter = FileConverter.class)
	private File settingsFile = new File("settings.json");
	
	@Parameter(names = {
			"-c",
			"--config"
	}, description = "The configuration file", converter = FileConverter.class)
	private File configurationFile = null;
	
	CLIParameters(){
	}
	
	File getConfigurationFile(){
		return this.configurationFile;
	}
	
	File getSettingsFile(){
		return this.settingsFile.getAbsoluteFile();
	}
}
