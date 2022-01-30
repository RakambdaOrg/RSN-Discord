package fr.raksrinana.rsndiscord.interaction.command.slash.impl.external;

import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubGroupSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.external.twitter.BlockCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.external.twitter.FetchCommand;
import org.jetbrains.annotations.NotNull;

public class TwitterSubCommandGroupCommand extends SubGroupSlashCommand{
	public TwitterSubCommandGroupCommand(){
		addSubcommand(new BlockCommand());
		addSubcommand(new FetchCommand());
	}
	
	@Override
	@NotNull
	public String getId(){
		return "twitter";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Twitter services";
	}
}
