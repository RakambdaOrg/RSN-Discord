package fr.raksrinana.rsndiscord.schedule;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.schedule.handler.IScheduleHandler;
import fr.raksrinana.rsndiscord.schedule.handler.ScheduleHandler;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.schedule.DeleteMessageScheduleConfiguration;
import fr.raksrinana.rsndiscord.settings.guild.schedule.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.utils.SortedList;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static fr.raksrinana.rsndiscord.utils.Utilities.*;
import static java.awt.Color.ORANGE;
import static java.time.Duration.between;

public class ScheduleUtils{
	private static final Collection<IScheduleHandler> handlers = new SortedList<>();
	
	/**
	 * Add a schedule into the configuration and send a notification message with a countdown.
	 *
	 * @param schedule The schedule to add.
	 * @param channel  The channel to send the message to.
	 */
	@NotNull
	public static CompletableFuture<Message> addScheduleAndNotify(@NotNull ScheduleConfiguration schedule, @NotNull TextChannel channel){
		return addScheduleAndNotify(schedule, channel, null);
	}
	
	/**
	 * Add a schedule into the configuration and send a notification message with a countdown.
	 *
	 * @param schedule The schedule to add.
	 * @param channel  The channel to send the message to.
	 */
	@NotNull
	public static CompletableFuture<Message> addScheduleAndNotify(@NotNull ScheduleConfiguration schedule, @NotNull TextChannel channel, @Nullable Consumer<EmbedBuilder> embedBuilderConsumer){
		addSchedule(channel.getGuild(), schedule);
		
		var content = translate(channel.getGuild(), "schedule.scheduled", schedule.getScheduleDate().format(DATE_TIME_MINUTE_FORMATTER));
		return JDAWrappers.message(channel, content)
				.embed(getEmbedFor(channel.getGuild(), schedule, embedBuilderConsumer))
				.submit()
				.thenApply(message -> {
					schedule.setReminderCountdownMessage(new MessageConfiguration(message));
					return message;
				});
	}
	
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
		Log.getLogger().info("Adding schedule handlers");
		getAllAnnotatedWith(ScheduleHandler.class, clazz -> (IScheduleHandler) clazz.getConstructor().newInstance())
				.peek(c -> Log.getLogger().info("Loaded schedule handler {}", c.getClass().getName()))
				.forEach(ScheduleUtils::addHandler);
	}
	
	public static void addHandler(@NotNull IScheduleHandler handler){
		handlers.add(handler);
	}
	
	@NotNull
	public static Consumer<Message> deleteMessage(@NotNull Function<ZonedDateTime, ZonedDateTime> applyDelay){
		return message -> deleteMessage(message, applyDelay);
	}
	
	@NotNull
	public static Consumer<Message> deleteMessageMins(int minutes){
		return deleteMessage(date -> date.plusMinutes(minutes));
	}
	
	public static void deleteMessage(@NotNull Message message, @NotNull Function<ZonedDateTime, ZonedDateTime> applyDelay){
		addSchedule(message.getGuild(), new DeleteMessageScheduleConfiguration(
				message.getAuthor(),
				applyDelay.apply(ZonedDateTime.now()),
				message
		));
	}
	
	@NotNull
	public static Collection<IScheduleHandler> getHandlers(){
		return handlers;
	}
}
