package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.guild.rainbow6.NotificationChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import java.util.List;

public class Rainbow6ProLeagueConfigurationCommandComposite extends CommandComposite{
	public Rainbow6ProLeagueConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new NotificationChannelConfigurationCommand(this));
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Rainbow 6";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("r6", "rainbow6");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Rainbow 6 configurations";
	}
}
