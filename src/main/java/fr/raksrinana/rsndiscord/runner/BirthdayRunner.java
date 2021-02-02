package fr.raksrinana.rsndiscord.runner;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.concurrent.TimeUnit.HOURS;

@ScheduledRunner
public class BirthdayRunner implements IScheduledRunner{
	private final JDA jda;
	
	public BirthdayRunner(@NotNull JDA jda){
		this.jda = jda;
	}
	
	@Override
	public void execute(){
		var day = LocalDate.now();
		jda.getGuilds().forEach(guild -> {
			Log.getLogger(guild).info("Processing guild {}", guild);
			var birthdaysConfiguration = Settings.get(guild).getBirthdays();
			birthdaysConfiguration.getNotificationChannel()
					.flatMap(ChannelConfiguration::getChannel)
					.ifPresent(textChannel ->
							birthdaysConfiguration.getBirthdays().forEach((userConfiguration, birthday) -> {
								if(birthday.isAt(day) && !birthday.isNotified(day)){
									birthday.setNotified(day);
									
									userConfiguration.getUser()
											.ifPresent(user -> {
												var message = translate(guild, "birthday.today",
														user.getAsMention(),
														birthday.getDate().until(day).normalized().getYears());
												
												textChannel.sendMessage(message).submit();
											});
								}
							}));
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
