package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
@SuppressWarnings("WeakerAccess")
public class TempParticipationCommand extends BasicCommand{
	public static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyyMMdd");
	public static final DateTimeFormatter DFD = DateTimeFormatter.ofPattern("dd/MM/yyy");
	
	@Nonnull
	@Override
	public CommandResult execute(@Nonnull final GuildMessageReceivedEvent event, @Nonnull final LinkedList<String> args) throws Exception{
		super.execute(event, args);
		sendInfos(event.getGuild(), LocalDate.now(), event.getAuthor(), event.getChannel());
		return CommandResult.SUCCESS;
	}
	
	public static boolean sendInfos(@Nonnull final Guild guild, @Nonnull final LocalDate localDate, @Nonnull final User author, @Nonnull final TextChannel channel){
		return NewSettings.getConfiguration(guild).getParticipationConfiguration().getUsers(localDate, false).map(stats -> {
			final var position = new AtomicInteger(1);
			final var builder = Utilities.buildEmbed(author, Color.MAGENTA, "Participation of the " + localDate.format(DFD) + " (UTC)");
			stats.getScores().stream().sorted((e1, e2) -> Long.compare(e2.getScore(), e1.getScore())).forEachOrdered(e -> builder.addField("#" + position.getAndIncrement(), Optional.ofNullable(guild.getJDA().getUserById(e.getId())).map(User::getAsMention).or(e::getName).orElse("<<UNKNOWN>>") + " Messages: " + e.getScore(), false));
			Actions.sendMessage(channel, builder.build());
			return true;
		}).orElse(false);
	}
	
	@Nonnull
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
		return "Temporary participation";
	}
	
	@Nonnull
	@SuppressWarnings("SpellCheckingInspection")
	@Override
	public List<String> getCommandStrings(){
		return List.of("tempparticipation", "tp");
	}
	
	@Nonnull
	@Override
	public String getDescription(){
		return "Display the temporary ranking for the day";
	}
	
	@Override
	public int getScope(){
		return ChannelType.TEXT.getId();
	}
}
