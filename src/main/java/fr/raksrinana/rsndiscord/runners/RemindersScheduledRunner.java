package fr.raksrinana.rsndiscord.runners;

import fr.raksrinana.rsndiscord.commands.ReminderCommand;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class RemindersScheduledRunner implements ScheduledRunner{
	@Getter
	private final JDA jda;
	
	public RemindersScheduledRunner(JDA jda){
		this.jda = jda;
		Log.getLogger(null).info("Creating reminders runner");
	}
	
	@Override
	public void run(){
		Log.getLogger(null).info("Starting reminders runner");
		final var currentDate = LocalDateTime.now();
		for(final var guild : this.getJda().getGuilds()){
			Log.getLogger(guild).debug("Processing guild {}", guild);
			final var it = Settings.get(guild).getReminders().iterator();
			while(it.hasNext()){
				final var reminder = it.next();
				if(currentDate.isAfter(reminder.getNotifyDate())){
					reminder.getUser().getUser().ifPresent(user -> reminder.getChannel().getChannel().ifPresentOrElse(channel -> {
						Actions.sendMessage(channel, MessageFormat.format("Reminder for {0}: {1}", user.getAsMention(), reminder.getMessage()), null);
						Optional.ofNullable(reminder.getReminderCountdownMessage()).flatMap(MessageConfiguration::getMessage).ifPresent(Actions::deleteMessage);
						it.remove();
					}, it::remove));
				}
				else{
					final var embed = ReminderCommand.getEmbedFor(reminder);
					Optional.ofNullable(reminder.getReminderCountdownMessage()).flatMap(MessageConfiguration::getMessage).ifPresentOrElse(message -> Actions.editMessage(message, embed), () -> reminder.getChannel().getChannel().ifPresent(channel -> Actions.sendMessage(channel, "", embed).thenAccept(message -> reminder.setReminderCountdownMessage(new MessageConfiguration(message)))));
				}
			}
		}
		Log.getLogger(null).info("Reminders role done");
	}
	
	@Override
	public long getDelay(){
		return 3;
	}
	
	@Override
	public long getPeriod(){
		return 14;
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.MINUTES;
	}
}
