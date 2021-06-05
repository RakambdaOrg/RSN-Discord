package fr.raksrinana.rsndiscord.runner;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.birthday.BirthdaysConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.concurrent.TimeUnit.HOURS;

@ScheduledRunner
@Log4j2
public class BirthdayRunner implements IScheduledRunner{
	private final JDA jda;
	
	public BirthdayRunner(@NotNull JDA jda){
		this.jda = jda;
	}
	
	@Override
	public void execute(){
		jda.getGuilds().forEach(guild -> {
			log.info("Processing guild {}", guild);
			var birthdaysConfiguration = Settings.get(guild).getBirthdays();
			
			birthdaysConfiguration.getNotificationChannel()
					.flatMap(ChannelConfiguration::getChannel)
					.ifPresent(textChannel -> sendBirthdays(textChannel, birthdaysConfiguration));
		});
	}
	
	private void sendBirthdays(@NotNull TextChannel channel, @NotNull BirthdaysConfiguration birthdays){
		var day = LocalDate.now();
		
		birthdays.getBirthdays().forEach((userConfiguration, birthday) -> {
			if(birthday.isAt(day) && !birthday.isNotified(day)){
				birthday.setNotified(day);
				
				userConfiguration.getUser().ifPresent(user -> {
					var years = birthday.getDate().until(day).normalized().getYears();
					var message = translate(channel.getGuild(), "birthday.today", user.getAsMention(), years);
					
					JDAWrappers.message(channel, message).submit();
				});
			}
		});
	}
	
	@Override
	public long getDelay(){
		return 0;
	}
	
	@NotNull
	@Override
	public String getName(){
		return "participation";
	}
	
	@Override
	public long getPeriod(){
		return 1;
	}
	
	@NotNull
	@Override
	public TimeUnit getPeriodUnit(){
		return HOURS;
	}
}
