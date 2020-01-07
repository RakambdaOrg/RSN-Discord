package fr.raksrinana.rsndiscord.commands.stopwatch;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.listeners.reply.ReplyMessageListener;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

@BotCommand
public class StopwatchCommand extends BasicCommand{
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		final var builder = Utilities.buildEmbed(event.getAuthor(), Color.GREEN, "Stopwatch", null);
		builder.addField("Time", "", false);
		builder.addField(BasicEmotes.P.getValue(), "Pause", true);
		builder.addField(BasicEmotes.R.getValue(), "Resume", true);
		builder.addField(BasicEmotes.S.getValue(), "Stop", true);
		Actions.sendMessage(event.getChannel(), "", builder.build()).thenAccept(message -> {
			Actions.addReaction(message, BasicEmotes.P.getValue());
			Actions.addReaction(message, BasicEmotes.S.getValue());
			ReplyMessageListener.handleReply(new StopwatchWaitingUserReply(event, message));
		});
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.MODERATOR;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Stopwatch";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("stopwatch");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "A stopwatch";
	}
}
