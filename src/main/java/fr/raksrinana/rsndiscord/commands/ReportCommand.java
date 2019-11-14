package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

@BotCommand
public class ReportCommand extends BasicCommand{
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Reason", "The reason of the report", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		if(args.isEmpty()){
			return CommandResult.BAD_ARGUMENTS;
		}
		return Settings.get(event.getGuild()).getReportChannel().flatMap(ChannelConfiguration::getChannel).map(channel -> {
			final var builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.ORANGE);
			builder.setTitle("New report");
			builder.addField("User", event.getAuthor().getAsMention(), false);
			builder.addField("Reason", String.join(" ", args), false);
			builder.setTimestamp(event.getMessage().getTimeCreated());
			Actions.sendMessage(channel, "", builder.build()).thenAccept(message -> Actions.replyPrivate(event.getGuild(), event.getAuthor(), "Your message have been forwarded.", null));
			return CommandResult.SUCCESS;
		}).orElse(CommandResult.FAILED);
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " <reason...>";
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Report";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("report", "r");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Sends a message to the staff";
	}
}
