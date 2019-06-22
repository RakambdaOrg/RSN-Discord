package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.configs.EmotesParticipationConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import javax.annotation.Nonnull;
import java.awt.Color;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
@SuppressWarnings("WeakerAccess")
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
		final var weekKey = getKey(localDate);
		final var date = localDate.format(DFD);
		return new EmotesParticipationConfig(guild).getValue(weekKey).map(stats -> {
			final var position = new AtomicInteger(1);
			final var builder = Utilities.buildEmbed(author, Color.MAGENTA, "Participation of the week " + date + " (UTC)");
			stats.entrySet().stream().sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())).map(e -> guild.getEmotesByName(e.getKey(), true).stream().findFirst().map(e2 -> Map.entry(e2, e.getValue())).orElse(null)).filter(Objects::nonNull).limit(limit).forEachOrdered(e -> builder.addField("#" + position.getAndIncrement(), e.getKey().getAsMention() + " Use count: " + e.getValue(), false));
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
