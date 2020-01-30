package fr.raksrinana.rsndiscord.commands.rainbow6;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.runners.Rainbow6ProLeagueScheduledRunner;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;

public class Rainbow6FetchMatchesCommand extends BasicCommand{
	public Rainbow6FetchMatchesCommand(final Command parent){
		super(parent);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		new Rainbow6ProLeagueScheduledRunner(event.getJDA()).run();
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Fetch matches";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("m", "match");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Fetches the matches of the pro league";
	}
}
