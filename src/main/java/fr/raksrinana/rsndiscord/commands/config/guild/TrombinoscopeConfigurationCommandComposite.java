package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.guild.trombinoscope.PicturesChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.trombinoscope.PosterRoleConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import java.util.List;

public class TrombinoscopeConfigurationCommandComposite extends CommandComposite{
	public TrombinoscopeConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new PicturesChannelConfigurationCommand(this));
		this.addSubCommand(new PosterRoleConfigurationCommand(this));
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Trombinoscope";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("trombinoscope", "trombi");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Trombinoscope configurations";
	}
}
