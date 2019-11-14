package fr.raksrinana.rsndiscord.commands.config.guild;

import fr.raksrinana.rsndiscord.commands.config.guild.twitch.TwitchAutoConnectUsersConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.config.guild.twitch.TwitchChannelConfigurationCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import lombok.NonNull;
import java.util.List;

public class TwitchConfigurationCommandComposite extends CommandComposite{
	public TwitchConfigurationCommandComposite(final Command parent){
		super(parent);
		this.addSubCommand(new TwitchChannelConfigurationCommand(this));
		this.addSubCommand(new TwitchAutoConnectUsersConfigurationCommand(this));
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Twitch";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("tw", "twitch");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Twitch configurations";
	}
}
