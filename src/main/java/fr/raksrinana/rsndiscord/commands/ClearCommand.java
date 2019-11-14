package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@BotCommand
public class ClearCommand extends BasicCommand{
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("count", "The number of messages to delete (default: 100)", false);
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
		event.getChannel().getIterableHistory().stream().limit(messageCount).forEach(Actions::deleteMessage);
		return CommandResult.SUCCESS;
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [count]";
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
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
