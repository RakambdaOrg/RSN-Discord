package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
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

@BotCommand
public class TempParticipationCommand extends BasicCommand{
	public static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyyMMdd");
	public static final DateTimeFormatter DFD = DateTimeFormatter.ofPattern("dd/MM/yyy");
	
	@NonNull
	public static String getKey(@NonNull final LocalDate localDate){
		return localDate.format(DF);
	}
	
	@NonNull
	@Override
	public CommandResult execute(@NonNull final GuildMessageReceivedEvent event, @NonNull final LinkedList<String> args){
		super.execute(event, args);
		sendInfos(event.getGuild(), LocalDate.now(), event.getAuthor(), event.getChannel(), 25);
		return CommandResult.SUCCESS;
	}
	
	public static boolean sendInfos(@NonNull final Guild guild, @NonNull final LocalDate localDate, @NonNull final User author, @NonNull final TextChannel channel, final int limit){
		return Settings.get(guild).getParticipationConfig().getUsers(localDate, false).map(stats -> {
			final var position = new AtomicInteger(1);
			final var builder = Utilities.buildEmbed(author, Color.MAGENTA, "Participation of the " + localDate.format(DFD) + " (UTC)", null);
			stats.getScores().stream().sorted((e1, e2) -> Long.compare(e2.getScore(), e1.getScore())).limit(limit).forEachOrdered(e -> builder.addField("#" + position.getAndIncrement(), Optional.ofNullable(guild.getJDA().getUserById(e.getId())).map(User::getAsMention).or(e::getName).orElse("<<UNKNOWN>>") + " Messages: " + e.getScore(), false));
			Actions.sendMessage(channel, "", builder.build());
			return true;
		}).orElse(false);
	}
	
	@NonNull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Temporary participation";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("tempparticipation", "tp");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Display the temporary ranking for the day";
	}
}
