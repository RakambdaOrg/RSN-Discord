package fr.raksrinana.rsndiscord.commands.trakt;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.runners.trakt.TraktUserHistoryScheduledRunner;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;

class TestCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	TestCommand(final Command parent){
		super(parent);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		new TraktUserHistoryScheduledRunner(event.getJDA()).runQueryOnDefaultUsersChannels();
		return CommandResult.FAILED;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Trakt test";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("test");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Test";
	}
}
