package fr.raksrinana.rsndiscord.interaction.command.slash.impl.external;

import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubGroupSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.external.twitch.ConnectCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.external.twitch.DisconnectCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.external.twitch.QuitCommand;
import org.jetbrains.annotations.NotNull;

public class TwitchSubCommandGroupCommand extends SubGroupSlashCommand{
	public TwitchSubCommandGroupCommand(){
		addSubcommand(new ConnectCommand());
		addSubcommand(new DisconnectCommand());
		addSubcommand(new QuitCommand());
	}
	
	@Override
	@NotNull
	public String getId(){
		return "twitch";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Twitch services";
	}
}
