package fr.raksrinana.rsndiscord.runner;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.settings.Settings;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.MINUTES;

@ScheduledRunner
public class AutoDeleteRunner implements IScheduledRunner{
	private final JDA jda;
	
	public AutoDeleteRunner(@NotNull JDA jda){
		this.jda = jda;
	}
	
	@Override
	public void execute(){
		jda.getGuilds().forEach(guild -> {
			Log.getLogger(guild).info("Processing guild {}", guild);
			Settings.get(guild).getAutoDeleteChannels()
					.entrySet().stream()
					.map(entry -> {
						var channel = entry.getKey().getChannel();
						var duration = Duration.of(entry.getValue(), ChronoUnit.MINUTES);
						return channel.map(textChannel -> Map.entry(textChannel, duration)).orElse(null);
					})
					.filter(Objects::nonNull)
					.forEach(entry -> {
						var limitDate = OffsetDateTime.now().minus(entry.getValue());
						entry.getKey().getIterableHistory().stream()
								.filter(message -> message.getTimeCreated().isBefore(limitDate))
								.forEach(message -> message.delete().queue());
					});
		});
	}
	
	@Override
	public long getDelay(){
		return 0;
	}
	
	@NotNull
	@Override
	public String getName(){
		return "autoDelete";
	}
	
	@Override
	public long getPeriod(){
		return 10;
	}
	
	@NotNull
	@Override
	public TimeUnit getPeriodUnit(){
		return MINUTES;
	}
}
