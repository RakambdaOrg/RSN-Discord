package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.command.BotSlashCommand;
import fr.raksrinana.rsndiscord.command.base.group.SubCommandsGroupGroupCommand;
import fr.raksrinana.rsndiscord.command.impl.external.AnilistSubCommandGroupCommand;
import fr.raksrinana.rsndiscord.command.impl.external.TraktSubCommandGroupCommand;
import fr.raksrinana.rsndiscord.command.impl.external.TwitchSubCommandGroupCommand;
import fr.raksrinana.rsndiscord.command.impl.external.TwitterSubCommandGroupCommand;
import org.jetbrains.annotations.NotNull;

@BotSlashCommand
public class ExternalGroupCommand extends SubCommandsGroupGroupCommand{
	public ExternalGroupCommand(){
		addSubcommandGroup(new AnilistSubCommandGroupCommand());
		addSubcommandGroup(new TraktSubCommandGroupCommand());
		addSubcommandGroup(new TwitchSubCommandGroupCommand());
		addSubcommandGroup(new TwitterSubCommandGroupCommand());
	}
	
	@Override
	@NotNull
	public String getId(){
		return "external";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "External services";
	}
	
	@Override
	public boolean getDefaultPermission(){
		return false;
	}
}
