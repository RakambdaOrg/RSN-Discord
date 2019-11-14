package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.guild.overwatch.NotificationChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import java.util.List;

public class OverwatchConfigurationCommandComposite extends CommandComposite{
	public OverwatchConfigurationCommandComposite(final Command parent){
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
		return "Overwatch";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("ow", "overwatch");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Overwatch configurations";
	}
}
