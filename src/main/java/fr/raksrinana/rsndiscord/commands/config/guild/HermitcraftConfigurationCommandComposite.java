package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.guild.hermitcraft.StreamingNotificationChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.hermitcraft.VideoNotificationChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import java.util.List;

public class HermitcraftConfigurationCommandComposite extends CommandComposite{
	public HermitcraftConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new VideoNotificationChannelConfigurationCommand(this));
		this.addSubCommand(new StreamingNotificationChannelConfigurationCommand(this));
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Hermitcraft";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("hermitcraft");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Hermitcraft configurations";
	}
}
