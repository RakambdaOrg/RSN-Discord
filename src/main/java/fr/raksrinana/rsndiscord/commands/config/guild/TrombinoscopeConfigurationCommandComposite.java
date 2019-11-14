package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.guild.trombinoscope.TrombinoscopeChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.trombinoscope.TrombinoscopeRoleConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import java.util.List;

public class TrombinoscopeConfigurationCommandComposite extends CommandComposite{
	public TrombinoscopeConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new TrombinoscopeChannelConfigurationCommand(this));
		this.addSubCommand(new TrombinoscopeRoleConfigurationCommand(this));
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
		return List.of("trombinoscope");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Trombinoscope configurations";
	}
}
