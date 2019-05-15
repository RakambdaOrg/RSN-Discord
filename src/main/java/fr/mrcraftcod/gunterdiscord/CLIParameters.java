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
			"-t",
			"--token"
	}, description = "The token to use")
	private String token = null;
	
	@Parameter(names = {
			"-tf",
			"--token-file"
	}, description = "The settings file for the token", converter = FileConverter.class)
	private File tokenFile = null;
	
	public CLIParameters(){
	}
	
	public File getSettingsFile(){
		return this.settingsFile.getAbsoluteFile();
	}
	
	public String getToken(){
		return token;
	}
	
	public File getTokenFile(){
		return tokenFile;
	}
}
