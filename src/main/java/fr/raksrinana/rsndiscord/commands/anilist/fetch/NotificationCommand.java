package fr.raksrinana.rsndiscord.commands.anilist.fetch;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.Command;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.runners.anilist.AniListNotificationScheduledRunner;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;

class NotificationCommand extends BasicCommand{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	NotificationCommand(final Command parent){
		super(parent);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		new AniListNotificationScheduledRunner(event.getJDA()).runQueryOnDefaultUsersChannels();
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "AniList fetch notification";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("notification", "n");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Fetch notifications";
	}
}
