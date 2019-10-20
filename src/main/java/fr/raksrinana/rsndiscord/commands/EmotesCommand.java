package fr.raksrinana.rsndiscord.commands;

import fr.raksrinana.rsndiscord.commands.generic.BasicCommand;
import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandResult;
import fr.raksrinana.rsndiscord.listeners.LogListener;
import fr.raksrinana.rsndiscord.settings.NewSettings;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import java.awt.Color;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
@SuppressWarnings("WeakerAccess")
@BotCommand
public class EmotesCommand extends BasicCommand{
	@SuppressWarnings("SpellCheckingInspection")
	public static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyyww");
	public static final DateTimeFormatter DFD = DateTimeFormatter.ofPattern("ww");
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		sendInfos(event.getGuild(), LocalDate.now(), event.getAuthor(), event.getChannel(), Optional.ofNullable(args.poll()).map(e -> {
			try{
				return Integer.parseInt(e);
			}
			catch(Exception ignored){
			}
			return null;
		}).filter(i -> i > 0).orElse(10));
		return CommandResult.SUCCESS;
	}
	
	public static boolean sendInfos(@Nonnull final Guild guild, @Nonnull final LocalDate localDate, @Nonnull final User author, @Nonnull final TextChannel channel, final int limit){
		final var weekKey = localDate.minusDays(LogListener.getDaysToRemove(localDate.getDayOfWeek()));
		return NewSettings.getConfiguration(guild).getParticipationConfiguration().getEmotes(weekKey, false).map(stats -> {
			final var position = new AtomicInteger(1);
			final var builder = Utilities.buildEmbed(author, Color.MAGENTA, "Participation of the week " + localDate.format(DFD) + " (UTC)");
			stats.getScores().stream().sorted((e1, e2) -> Long.compare(e2.getScore(), e1.getScore())).limit(limit).forEachOrdered(e -> builder.addField("#" + position.getAndIncrement(), Optional.ofNullable(guild.getEmoteById(e.getId())).map(Emote::getAsMention).or(e::getName).orElse("<<UNKNOWN>>") + " Use count: " + e.getScore(), false));
			Actions.sendMessage(channel, builder.build());
			return true;
		}).orElse(false);
	}
	
	public static String getKey(@Nonnull final LocalDate localDate){
		return localDate.format(DF);
	}
	
	@Nonnull
	@Override
	public AccessLevel getAccessLevel(){
		return AccessLevel.ADMIN;
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "Utilisation emotes";
	}
	
	@Nonnull
	@Override
	public List<String> getCommandStrings(){
		return List.of("emote");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Gets the usage of the emotes for the current week";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
