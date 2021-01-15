package fr.raksrinana.rsndiscord.schedule;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.schedule.handler.IScheduleHandler;
import fr.raksrinana.rsndiscord.schedule.handler.ScheduleHandler;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.schedule.DeleteMessageScheduleConfiguration;
import fr.raksrinana.rsndiscord.settings.guild.schedule.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.utils.SortedList;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
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
	public static CompletableFuture<Message> addScheduleAndNotify(@NonNull ScheduleConfiguration schedule, @NonNull TextChannel channel){
		return addScheduleAndNotify(schedule, channel, null);
	}
	
	/**
	 * Add a schedule into the configuration and send a notification message with a countdown.
	 *
	 * @param schedule The schedule to add.
	 * @param channel  The channel to send the message to.
	 */
	public static CompletableFuture<Message> addScheduleAndNotify(@NonNull ScheduleConfiguration schedule, @NonNull TextChannel channel, Consumer<EmbedBuilder> embedBuilderConsumer){
		addSchedule(channel.getGuild(), schedule);
		
		var content = translate(channel.getGuild(), "schedule.scheduled", schedule.getScheduleDate().format(DATE_TIME_MINUTE_FORMATTER));
		return channel.sendMessage(content)
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
	public static void addSchedule(@NonNull Guild guild, @NonNull ScheduleConfiguration schedule){
		Settings.get(guild).addSchedule(schedule);
	}
	
	public static MessageEmbed getEmbedFor(@NonNull Guild guild, @NonNull ScheduleConfiguration reminder, Consumer<EmbedBuilder> embedBuilderConsumer){
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
	
	public static MessageEmbed getEmbedFor(@NonNull Guild guild, @NonNull ScheduleConfiguration reminder){
		return getEmbedFor(guild, reminder, null);
	}
	
	public static void registerAllHandlers(){
		getAllAnnotatedWith(ScheduleHandler.class, clazz -> (IScheduleHandler) clazz.getConstructor().newInstance())
				.peek(c -> Log.getLogger(null).info("Loaded schedule handler {}", c.getClass().getName()))
				.forEach(ScheduleUtils::addHandler);
	}
	
	public static void addHandler(@NonNull IScheduleHandler handler){
		handlers.add(handler);
	}
	
	public static Collection<IScheduleHandler> getHandlers(){
		return handlers;
	}
	
	public static Consumer<Message> deleteMessage(Function<ZonedDateTime, ZonedDateTime> applyDelay){
		return message -> deleteMessage(message, applyDelay);
	}
	
	public static void deleteMessage(Message message, Function<ZonedDateTime, ZonedDateTime> applyDelay){
		addSchedule(message.getGuild(), new DeleteMessageScheduleConfiguration(
				message.getAuthor(),
				applyDelay.apply(ZonedDateTime.now()),
				message
		));
	}
}
