package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.modules.permission.IPermission;
import fr.raksrinana.rsndiscord.modules.permission.SimplePermission;
import fr.raksrinana.rsndiscord.modules.schedule.ScheduleUtils;
import fr.raksrinana.rsndiscord.modules.schedule.config.DeleteMessageScheduleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@BotCommand
public class ClearCommand extends BasicCommand{
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("count", translate(guild, "command.clear.help.count", 100, 1000), false);
		builder.addField("channel", translate(guild, "command.clear.help.channel"), false);
	}
	
	@Override
	public @NonNull IPermission getPermission(){
		return new SimplePermission("command.clear", false);
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
		Actions.reply(event, translate(event.getGuild(), "clear.removing", messageCount, channel.getAsMention()), null).thenAccept(message -> ScheduleUtils.addSchedule(new DeleteMessageScheduleConfiguration(event.getAuthor(), ZonedDateTime.now().plusMinutes(2), message), event.getGuild()));
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
	public String getName(@NonNull Guild guild){
		return translate(guild, "command.clear.name");
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("clear");
	}
	
	@NonNull
	@Override
	public String getDescription(@NonNull Guild guild){
		return translate(guild, "command.clear.description");
	}
}
