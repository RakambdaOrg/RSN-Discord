package fr.rakambda.rsndiscord.spring.interaction.slash.impl.music;

import fr.rakambda.rsndiscord.spring.amqp.RabbitService;
import fr.rakambda.rsndiscord.spring.audio.AudioServiceFactory;
import fr.rakambda.rsndiscord.spring.audio.exception.NoTrackPlayingException;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandGuild;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import fr.rakambda.rsndiscord.spring.util.LocalizationService;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import static java.util.Objects.isNull;

@Slf4j
@Component
public class SeekCommand implements IExecutableSlashCommandGuild{
	public static final String TIME_OPTION_ID = "time";
	
	private static final Pattern TIME_PATTERN = Pattern.compile("((\\d{1,2}):)?((\\d{1,2}):)?(\\d{1,2})");
	private static final long SECOND_PER_MINUTE = 60;
	private static final long SECOND_PER_HOUR = 3600;
	
	private final AudioServiceFactory audioServiceFactory;
	private final LocalizationService localizationService;
	private final RabbitService rabbitService;
	
	@Autowired
	public SeekCommand(AudioServiceFactory audioServiceFactory, LocalizationService localizationService, RabbitService rabbitService){
		this.audioServiceFactory = audioServiceFactory;
		this.localizationService = localizationService;
		this.rabbitService = rabbitService;
	}
	
	@Override
	@NotNull
	public String getId(){
		return "seek";
	}
	
	@Override
	@NotNull
	public String getPath(){
		return "music/seek";
	}
	
	@Override
	@NotNull
	public CompletableFuture<?> executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member) throws NoTrackPlayingException{
		var deferred = event.deferReply().submit();
		var locale = event.getUserLocale();
		var time = SeekCommand.parseTime(event.getOption(TIME_OPTION_ID).getAsString());
		
		if(time < 0){
			var content = localizationService.translate(locale, "music.invalid-format");
			return deferred.thenCompose(empty -> JDAWrappers.edit(event, content)
					.submitAndDelete(5, rabbitService));
		}
		
		audioServiceFactory.get(guild).getTrackScheduler().seek(time);
		var content = localizationService.translate(locale, "music.seeked", event.getUser().getAsMention(), time);
		return deferred.thenCompose(empty -> JDAWrappers.edit(event, content)
				.submitAndDelete(5, rabbitService));
	}
	
	private static long parseTime(@NotNull String time){
		var matcher = TIME_PATTERN.matcher(time);
		if(!matcher.matches()){
			return -1;
		}
		var duration = 0L;
		duration += SeekCommand.getAsLong(matcher.group(2)) * SECOND_PER_HOUR;
		duration += SeekCommand.getAsLong(matcher.group(4)) * SECOND_PER_MINUTE;
		duration += SeekCommand.getAsLong(matcher.group(5));
		return duration * 1000;
	}
	
	private static long getAsLong(@Nullable String str){
		if(isNull(str) || str.isBlank()){
			return 0;
		}
		try{
			return Long.parseLong(str);
		}
		catch(Exception e){
			log.error("Error parsing {} into long", str, e);
		}
		return 0;
	}
}
