package fr.rakambda.rsndiscord.spring.interaction.slash.impl.bot;

import fr.rakambda.rsndiscord.spring.Application;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandGuild;
import fr.rakambda.rsndiscord.spring.interaction.slash.api.IExecutableSlashCommandUser;
import fr.rakambda.rsndiscord.spring.jda.JDAWrappers;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import static java.awt.Color.GREEN;
import static java.time.Duration.between;
import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;

@Slf4j
@Component
public class GeneralCommand implements IExecutableSlashCommandGuild, IExecutableSlashCommandUser{
	private final ZonedDateTime startTime = ZonedDateTime.now();
	
	private final String commitId;
	
	public GeneralCommand(){
		var gitProperties = loadProperties("/git.properties");
		commitId = gitProperties.getProperty("git.commit.id", "Unknown");
	}
	
	@NonNull
	private Properties loadProperties(@NonNull String resource){
		var properties = new Properties();
		try{
			var versionPropertiesFile = Application.class.getResource(resource);
			if(Objects.nonNull(versionPropertiesFile)){
				try(var is = versionPropertiesFile.openStream()){
					properties.load(is);
				}
			}
		}
		catch(Exception e){
			log.warn("Error reading properties from {}", resource, e);
		}
		return properties;
	}
	
	@Override
	@NonNull
	public String getId(){
		return "general";
	}
	
	@Override
	@NonNull
	public String getPath(){
		return "bot/general";
	}
	
	@Override
	@NonNull
	public CompletableFuture<?> executeGuild(@NonNull SlashCommandInteraction event, @NonNull Guild guild, @NonNull Member member){
		return execute(event);
	}
	
	@Override
	@NonNull
	public CompletableFuture<?> executeUser(@NonNull SlashCommandInteraction event){
		return execute(event);
	}
	
	@NonNull
	private CompletableFuture<Message> execute(@NonNull SlashCommandInteraction event){
		var now = ZonedDateTime.now();
		var embed = new EmbedBuilder()
				.setColor(GREEN)
				.addField("Commit id", commitId, false)
				.addField("Current time", now.format(ISO_ZONED_DATE_TIME), false)
				.addField("Last start", startTime.format(ISO_ZONED_DATE_TIME), false)
				.addField("Time elapsed", durationToString(between(startTime, now)), false)
				.build();
		
		return JDAWrappers.reply(event, embed).ephemeral(true).submit();
	}
	
	@NonNull
	private String durationToString(@NonNull Duration duration){
		if(duration.toDaysPart() > 0){
			return String.format("%dd %dh%02dm%02ds", duration.toDaysPart(), duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
		}
		if(duration.toHoursPart() > 0){
			return String.format("%dh%02dm%02ds", duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
		}
		if(duration.toMinutesPart() > 0){
			return String.format("%02dm%02ds", duration.toMinutesPart(), duration.toSecondsPart());
		}
		return String.format("%02ds", duration.toSecondsPart());
	}
}
