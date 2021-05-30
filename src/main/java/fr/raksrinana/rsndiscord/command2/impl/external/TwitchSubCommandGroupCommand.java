package fr.raksrinana.rsndiscord.command2.impl.external;

import fr.raksrinana.rsndiscord.command2.base.group.SubCommandGroup;
import fr.raksrinana.rsndiscord.command2.impl.external.twitch.ConnectCommand;
import fr.raksrinana.rsndiscord.command2.impl.external.twitch.DisconnectCommand;
import fr.raksrinana.rsndiscord.command2.impl.external.twitch.QuitCommand;
import org.jetbrains.annotations.NotNull;

public class TwitchSubCommandGroupCommand extends SubCommandGroup{
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
