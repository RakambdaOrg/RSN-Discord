package fr.raksrinana.rsndiscord.commands.schedule;

import fr.raksrinana.rsndiscord.commands.generic.BotCommand;
import fr.raksrinana.rsndiscord.commands.generic.CommandComposite;
import fr.raksrinana.rsndiscord.commands.schedule.delete.DeleteCommandComposite;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@BotCommand
public class ScheduleCommandComposite extends CommandComposite{
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'!'HH:mm");
	private static final Pattern PERIOD_PATTERN = Pattern.compile("([0-9]+)([mhd])");
	
	public ScheduleCommandComposite(){
		this.addSubCommand(new MessageScheduleCommand(this));
		this.addSubCommand(new DeleteCommandComposite(this));
	}
	
	public static Optional<LocalDateTime> getReminderDate(@NonNull String string){
		try{
			final var duration = parsePeriod(string);
			if(!duration.isZero()){
				return Optional.of(LocalDateTime.now().plus(duration));
			}
			return Optional.of(LocalDateTime.parse(string, DATE_FORMATTER));
		}
		catch(DateTimeParseException ignored){
		}
		return Optional.empty();
	}
	
	private static Duration parsePeriod(@NonNull String period){
		period = period.toLowerCase(Locale.ENGLISH);
		Matcher matcher = PERIOD_PATTERN.matcher(period);
		Duration duration = Duration.ZERO;
		while(matcher.find()){
			int amount = Integer.parseInt(matcher.group(1));
			String type = matcher.group(2);
			switch(type){
				case "m":
					duration = duration.plus(Duration.ofMinutes(amount));
					break;
				case "h":
					duration = duration.plus(Duration.ofHours(amount));
					break;
				case "d":
					duration = duration.plus(Duration.ofDays(amount));
					break;
			}
		}
		return duration;
	}
	
	@Override
	public void addHelp(@NonNull Guild guild, @NonNull EmbedBuilder builder){
		super.addHelp(guild, builder);
		builder.addField("delay", "The delay before executing the action in the format `XdXhXm` where Xs are numbers or `yyyy-MM-dd!HH:mm`", false);
	}
	
	@NonNull
	@Override
	public String getName(){
		return "Schedule";
	}
	
	@NonNull
	@Override
	public List<String> getCommandStrings(){
		return List.of("schedule");
	}
	
	@NonNull
	@Override
	public String getDescription(){
		return "Scheduling related commands";
	}
}
