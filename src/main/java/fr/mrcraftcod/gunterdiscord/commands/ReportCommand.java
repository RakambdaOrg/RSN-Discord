package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.ReportChannelConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
public class ReportCommand extends BasicCommand{
	@Override
	public void addHelp(@NotNull final Guild guild, @NotNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Reason", "The reason of the report", false);
	}
	
	@Override
	public CommandResult execute(@NotNull final MessageReceivedEvent event, @NotNull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		final var channel = new ReportChannelConfig(event.getGuild()).getObject();
		if(Objects.isNull(channel)){
			return CommandResult.FAILED;
		}
		else{
			final var builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.ORANGE);
			builder.setTitle("New report");
			builder.addField("User", event.getAuthor().getAsMention(), false);
			builder.addField("Reason", String.join(" ", args), false);
			builder.setTimestamp(event.getMessage().getTimeCreated());
			Actions.sendMessage(channel, builder.build());
			Actions.replyPrivate(event.getGuild(), event.getAuthor(), "Your message have been forwarded.");
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <reason...>";
	}
	
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ALL;
	}
	
	@Override
	public String getName(){
		return "Report";
	}
	
	@Override
	public List<String> getCommand(){
		return List.of("report", "r");
	}
	
	@Override
	public String getDescription(){
		return "Sends a message to the staff";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
