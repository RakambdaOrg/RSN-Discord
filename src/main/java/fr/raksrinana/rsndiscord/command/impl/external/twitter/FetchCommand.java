package fr.raksrinana.rsndiscord.command.impl.external.twitter;

import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command.base.group.SubCommand;
import fr.raksrinana.rsndiscord.command.permission.IPermission;
import fr.raksrinana.rsndiscord.runner.impl.twitter.UserTweetsRunner;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.command.CommandResult.FAILED;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED_NO_MESSAGE;
import static fr.raksrinana.rsndiscord.command.permission.SimplePermission.FALSE_BY_DEFAULT;

@Log4j2
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
	public CommandResult executeGuild(@NotNull SlashCommandInteractionEvent event, @NotNull Guild guild, @NotNull Member member){
		try{
			new UserTweetsRunner().executeGuild(guild);
			return HANDLED_NO_MESSAGE;
		}
		catch(Exception e){
			log.error("Failed updating tweets", e);
			return FAILED;
		}
	}
}
