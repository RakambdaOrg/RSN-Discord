package fr.raksrinana.rsndiscord.interaction.command.slash.impl;

import fr.raksrinana.rsndiscord.interaction.command.slash.api.BotSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubCommandsGroupGroupSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.external.AnilistSubCommandGroupCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.external.TraktSubCommandGroupCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.external.TwitchSubCommandGroupCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.external.TwitterSubCommandGroupCommand;
import org.jetbrains.annotations.NotNull;

@BotSlashCommand
public class ExternalGroupCommand extends SubCommandsGroupGroupSlashCommand{
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