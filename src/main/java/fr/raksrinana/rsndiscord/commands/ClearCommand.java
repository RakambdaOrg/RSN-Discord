package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
@BotCommand
public class ClearCommand extends BasicCommand{
	@Override
	public void addHelp(@Nonnull final Guild guild, @Nonnull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("count", "The number of messages to delete (default: 100)", false);
	}
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		final var messageCount = Optional.ofNullable(args.poll()).map(arg -> {
			try{
				return Integer.parseInt(arg);
			}
			catch(NumberFormatException ignored){
			}
			return null;
		}).orElse(100);
		event.getChannel().getIterableHistory().stream().limit(messageCount).forEach(message -> message.delete().queue());
		return CommandResult.SUCCESS;
	}
	
	@Nonnull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [count]";
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Clear";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("clear");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Clear messages in a channel";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
