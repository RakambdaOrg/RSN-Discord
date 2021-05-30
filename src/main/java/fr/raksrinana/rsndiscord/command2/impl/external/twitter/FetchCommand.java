package fr.raksrinana.rsndiscord.command2.impl.external.twitter;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommand;
import fr.raksrinana.rsndiscord.command2.permission.IPermission;
import fr.raksrinana.rsndiscord.runner.twitter.UserTweetsRunner;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS_NO_MESSAGE;
import static fr.raksrinana.rsndiscord.command2.permission.SimplePermission.FALSE_BY_DEFAULT;

public class FetchCommand extends SubCommand{
	@Override
	@NotNull
	public String getId(){
		return "fetch";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Fetch tweets";
	}
	
	@Override
	@NotNull
	public IPermission getPermission(){
		return FALSE_BY_DEFAULT;
	}
	
	@Override
	@NotNull
	public CommandResult execute(@NotNull SlashCommandEvent event){
		new UserTweetsRunner(event.getJDA()).execute();
		return SUCCESS_NO_MESSAGE;
	}
}
