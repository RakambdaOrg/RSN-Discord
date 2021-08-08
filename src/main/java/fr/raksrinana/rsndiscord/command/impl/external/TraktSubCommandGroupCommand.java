package fr.raksrinana.rsndiscord.command.impl.external;

import fr.raksrinana.rsndiscord.command.base.group.SubCommandGroup;
import fr.raksrinana.rsndiscord.command.impl.external.trakt.RegisterCommand;
import org.jetbrains.annotations.NotNull;

public class TraktSubCommandGroupCommand extends SubCommandGroup{
	public TraktSubCommandGroupCommand(){
		addSubcommand(new RegisterCommand());
	}
	
	@Override
	@NotNull
	public String getId(){
		return "trakt";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Trakt services";
	}
}
