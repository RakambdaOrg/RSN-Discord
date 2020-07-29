package fr.raksrinana.rsndiscord.runners;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

public class BirthdayScheduledRunner implements ScheduledRunner{
	private final JDA jda;
	
	public BirthdayScheduledRunner(JDA jda){
		this.jda = jda;
	}
	
	@Override
	public void execute(){
		final var day = LocalDate.now();
		this.jda.getGuilds().forEach(guild -> {
			Log.getLogger(guild).info("Processing guild {}", guild);
			var birthdaysConfiguration = Settings.get(guild).getBirthdays();
			birthdaysConfiguration.getNotificationChannel().flatMap(ChannelConfiguration::getChannel).ifPresent(textChannel -> {
				birthdaysConfiguration.getBirthdays().forEach((userConfiguration, birthday) -> {
					if(birthday.isAt(day) && !birthday.isNotified(day)){
						birthday.setNotified(day);
						userConfiguration.getUser().ifPresent(user -> {
							Actions.sendMessage(textChannel, translate(guild, "birthday.today", user.getAsMention(), birthday.getDate().until(day).normalized().getYears()), null);
						});
					}
				});
			});
		});
	}
	
	@Override
	public long getDelay(){
		return 0;
	}
	
	@NonNull
	@Override
	public String getName(){
		return "participation";
	}
	
	@Override
	public long getPeriod(){
		return 1;
	}
	
	@NonNull
	@Override
	public TimeUnit getPeriodUnit(){
		return TimeUnit.HOURS;
	}
}
