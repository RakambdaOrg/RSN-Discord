package fr.raksrinana.rsndiscord.schedule;

import fr.raksrinana.rsndiscord.schedule.handler.IScheduleHandler;
import fr.raksrinana.rsndiscord.schedule.handler.ScheduleHandler;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.schedule.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.utils.SortedList;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static fr.raksrinana.rsndiscord.utils.Utilities.*;
import static java.awt.Color.ORANGE;
import static java.time.Duration.between;
@Log4j2
public class ScheduleUtils{
	private static final Collection<IScheduleHandler> handlers = new SortedList<>();
	
	/**
	 * Add a schedule into the configuration.
	 *
	 * @param guild    The guild where it is from.
	 * @param schedule The schedule to add.
	 */
	public static void addSchedule(@NotNull Guild guild, @NotNull ScheduleConfiguration schedule){
		Settings.get(guild).addSchedule(schedule);
	}
	
	@NotNull
	public static MessageEmbed getEmbedFor(@NotNull Guild guild, @NotNull ScheduleConfiguration reminder, @Nullable Consumer<EmbedBuilder> embedBuilderConsumer){
		var notifyDate = reminder.getScheduleDate();
		var author = reminder.getUser().getUser().orElse(guild.getJDA().getSelfUser());
		
		var builder = new EmbedBuilder().setAuthor(author.getName(), null, author.getAvatarUrl())
				.setColor(ORANGE)
				.setTitle(translate(guild, "reminder.reminder"))
				.addField(translate(guild, "reminder.date"), notifyDate.format(DATE_TIME_MINUTE_FORMATTER), true)
				.addField(translate(guild, "reminder.remaining"), durationToString(between(ZonedDateTime.now(), notifyDate)), true)
				.addField(translate(guild, "reminder.message"), reminder.getMessage(), false);
		
		if(Objects.nonNull(embedBuilderConsumer)){
			embedBuilderConsumer.accept(builder);
		}
		
		return builder.build();
	}
	
	@NotNull
	public static MessageEmbed getEmbedFor(@NotNull Guild guild, @NotNull ScheduleConfiguration reminder){
		return getEmbedFor(guild, reminder, null);
	}
	
	public static void registerAllHandlers(){
		log.info("Adding schedule handlers");
		getAllAnnotatedWith(ScheduleHandler.class, clazz -> (IScheduleHandler) clazz.getConstructor().newInstance())
				.peek(c -> log.info("Loaded schedule handler {}", c.getClass().getName()))
				.forEach(ScheduleUtils::addHandler);
	}
	
	public static void addHandler(@NotNull IScheduleHandler handler){
		handlers.add(handler);
	}
	
	@NotNull
	public static Collection<IScheduleHandler> getHandlers(){
		return handlers;
	}
}
