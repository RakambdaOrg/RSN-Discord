package fr.raksrinana.rsndiscord.commands.stopwatch;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.listeners.reply.ReplyMessageListener;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.Utilities;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

@BotCommand
public class StopwatchCommand extends BasicCommand{
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		final var builder = Utilities.buildEmbed(event.getAuthor(), Color.GREEN, "Stopwatch");
		builder.addField("Time", "", false);
		builder.addField(BasicEmotes.P.getValue(), "Pause", true);
		builder.addField(BasicEmotes.R.getValue(), "Resume", true);
		builder.addField(BasicEmotes.S.getValue(), "Stop", true);
		Actions.sendMessage(event.getChannel(), message -> {
			message.addReaction(BasicEmotes.P.getValue()).queue();
			message.addReaction(BasicEmotes.S.getValue()).queue();
			ReplyMessageListener.handleReply(new StopwatchWaitingUserReply(event, message));
		}, builder.build());
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.MODERATOR;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Stopwatch";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("stopwatch");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "A stopwatch";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
