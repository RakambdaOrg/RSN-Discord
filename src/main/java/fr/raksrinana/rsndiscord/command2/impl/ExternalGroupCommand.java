package fr.raksrinana.rsndiscord.command2.impl;

import fr.raksrinana.rsndiscord.command2.BotSlashCommand;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommandsGroupGroupCommand;
import fr.raksrinana.rsndiscord.command2.impl.external.AnilistSubCommandGroupCommand;
import fr.raksrinana.rsndiscord.command2.impl.external.TraktSubCommandGroupCommand;
import fr.raksrinana.rsndiscord.command2.impl.external.TwitchSubCommandGroupCommand;
import fr.raksrinana.rsndiscord.command2.impl.external.TwitterSubCommandGroupCommand;
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
