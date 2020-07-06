package fr.raksrinana.rsndiscord.utils.schedule;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.ScheduleConfiguration;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.SortedList;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class ScheduleUtils{
	private static final Collection<ScheduleHandler> handlers = new SortedList<>();
	
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
		addSchedule(schedule, channel.getGuild());
		return Actions.sendMessage(channel, translate(channel.getGuild(), "schedule.scheduled", schedule.getScheduleDate().format(Utilities.DATE_TIME_MINUTE_FORMATTER)), getEmbedFor(channel.getGuild(), schedule, embedBuilderConsumer)).thenApply(message -> {
			schedule.setReminderCountdownMessage(new MessageConfiguration(message));
			return message;
		});
	}
	
	/**
	 * Add a schedule into the configuration.
	 *
	 * @param schedule The schedule to add.
	 * @param guild    The guild where it is from.
	 */
	public static void addSchedule(@NonNull ScheduleConfiguration schedule, @NonNull Guild guild){
		Settings.get(guild).addSchedule(schedule);
	}
	
	public static MessageEmbed getEmbedFor(@NonNull Guild guild, @NonNull ScheduleConfiguration reminder, Consumer<EmbedBuilder> embedBuilderConsumer){
		final var notifyDate = reminder.getScheduleDate();
		final var builder = Utilities.buildEmbed(reminder.getUser().getUser().orElse(null), Color.ORANGE, translate(guild, "reminder.reminder"), null);
		builder.addField(translate(guild, "reminder.date"), notifyDate.format(Utilities.DATE_TIME_MINUTE_FORMATTER), true);
		builder.addField(translate(guild, "reminder.remaining"), Utilities.durationToString(Duration.between(ZonedDateTime.now(), notifyDate)), true);
		builder.addField(translate(guild, "reminder.message"), reminder.getMessage(), false);
		if(Objects.nonNull(embedBuilderConsumer)){
			embedBuilderConsumer.accept(builder);
		}
		return builder.build();
	}
	
	public static MessageEmbed getEmbedFor(@NonNull Guild guild, @NonNull ScheduleConfiguration reminder){
		return getEmbedFor(guild, reminder, null);
	}
	
	public static void registerAllHandlers(){
		Utilities.getAllInstancesOf(ScheduleHandler.class, Main.class.getPackage().getName() + ".utils.schedule", c -> {
			try{
				return c.getConstructor().newInstance();
			}
			catch(InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e){
				Log.getLogger(null).error("Failed to create instance of {}", c.getName(), e);
			}
			return null;
		}).stream().peek(c -> Log.getLogger(null).info("Loaded schedule handler {}", c.getClass().getName())).forEach(ScheduleUtils::addHandler);
	}
	
	public static void addHandler(@NonNull ScheduleHandler handler){
		handlers.add(handler);
	}
	
	public static Collection<ScheduleHandler> getHandlers(){
		return handlers;
	}
}
