package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.participation.MessageParticipation;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import java.awt.Color;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import static java.time.ZoneOffset.UTC;

@BotCommand
public class MessageParticipationCommand extends BasicCommand{
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	@Override
	public void addHelp(@NonNull final Guild guild, @NonNull final EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("Date", "The date of the data to get in the format YYYY-MM-DD (YYYY is the year, MM the month, DD the day).", false);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		final var maxUserCount = 25;
		final var day = Optional.ofNullable(args.poll()).map(arg -> LocalDate.parse(arg, DATE_FORMATTER)).orElse(LocalDate.now(UTC));
		Settings.get(event.getGuild()).getParticipationConfiguration().getDay(day).ifPresentOrElse(messageParticipation -> sendReport(maxUserCount, day, messageParticipation, event.getAuthor(), event.getChannel()), () -> Actions.reply(event, "No data found for this day", null));
		return CommandResult.SUCCESS;
	}
	
	public static void sendReport(int maxUserCount, LocalDate day, MessageParticipation messageParticipation, User author, TextChannel channel){
		channel.getGuild().retrieveMembers().thenAccept(empty -> {
			final var position = new AtomicInteger(0);
			final var embed = Utilities.buildEmbed(author, Color.GREEN, "Participation for the " + day.format(DATE_FORMATTER), null);
			messageParticipation.getUserCounts().entrySet().stream().sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())).limit(maxUserCount).forEachOrdered(entry -> embed.addField("#" + position.incrementAndGet() + " : " + entry.getValue(), String.valueOf(channel.getGuild().getMemberById(entry.getValue())), false));
			Actions.sendMessage(channel, "", embed.build());
		});
	}
	
	@NonNull
	@Override
	public String getCommandUsage(){
		return super.getCommandUsage() + " [date]";
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Participation";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("participation");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Get the messages participation of a day";
	}
}
