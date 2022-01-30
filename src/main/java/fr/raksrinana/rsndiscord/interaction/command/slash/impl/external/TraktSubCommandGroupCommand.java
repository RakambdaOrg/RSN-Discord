package fr.raksrinana.rsndiscord.interaction.command.slash.impl.external;

import fr.raksrinana.rsndiscord.interaction.command.slash.base.group.SubGroupSlashCommand;
import fr.raksrinana.rsndiscord.interaction.command.slash.impl.external.trakt.RegisterCommand;
import org.jetbrains.annotations.NotNull;

public class TraktSubCommandGroupCommand extends SubGroupSlashCommand{
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
