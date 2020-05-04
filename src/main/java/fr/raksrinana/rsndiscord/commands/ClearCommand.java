package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.guild.schedule.DeleteMessageScheduleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.schedule.ScheduleUtils;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@BotCommand
public class ClearCommand extends BasicCommand{
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("count", "The number of messages to delete (default: 100, max: 1000)", false);
		builder.addField("channel", "The channel to clear (default is the current channel)", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		final var messageCount = Optional.ofNullable(args.poll()).map(arg -> {
			try{
				return Integer.parseInt(arg);
			}
			catch(NumberFormatException ignored){
			}
			return null;
		}).orElse(100);
		final var channel = event.getMessage().getMentionedChannels().stream().findFirst().orElse(event.getChannel());
		Actions.reply(event, "Removing " + messageCount + " from channel " + channel.getAsMention(), null).thenAccept(message -> ScheduleUtils.addSchedule(new DeleteMessageScheduleConfiguration(event.getAuthor(), ZonedDateTime.now().plusMinutes(2), message), event.getGuild()));
		channel.getIterableHistory().takeAsync(messageCount).thenAccept(messages -> messages.forEach(Actions::deleteMessage));
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [count] [#channel]";
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.MODERATOR;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Clear";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("clear");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Clear messages in a channel";
	}
}
