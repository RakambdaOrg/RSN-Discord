package fr.raksrinana.rsndiscord.command2.impl.external;

import fr.raksrinana.rsndiscord.command2.base.group.SubCommandGroup;
import fr.raksrinana.rsndiscord.command2.impl.external.twitter.BlockCommand;
import fr.raksrinana.rsndiscord.command2.impl.external.twitter.FetchCommand;
import org.jetbrains.annotations.NotNull;

public class TwitterSubCommandGroupCommand extends SubCommandGroup{
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
