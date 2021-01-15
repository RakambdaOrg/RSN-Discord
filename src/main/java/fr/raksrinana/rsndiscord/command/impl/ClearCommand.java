package fr.raksrinana.rsndiscord.command.impl;

import fr.raksrinana.rsndiscord.command.BasicCommand;
import fr.raksrinana.rsndiscord.command.BotCommand;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.permission.IPermission;
import fr.raksrinana.rsndiscord.permission.SimplePermission;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.schedule.ScheduleUtils.deleteMessage;
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
		var channel = event.getChannel();
		
		var messageCount = getArgumentAsInteger(args).orElse(100);
		var targetChannel = getFirstChannelMentioned(event).orElse(channel);
		
		channel.sendMessage(translate(event.getGuild(), "clear.removing", messageCount, targetChannel.getAsMention())).submit()
				.thenAccept(deleteMessage(date -> date.plusMinutes(2)));
		
		targetChannel.getIterableHistory()
				.takeAsync(messageCount)
				.thenAccept(messages -> messages.forEach(message -> message.delete().submit()));
		return SUCCESS;
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
