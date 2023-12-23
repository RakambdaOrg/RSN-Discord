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
import org.jetbrains.annotations.NotNull;
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
	
	@NotNull
	private Properties loadProperties(@NotNull String resource){
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
	@NotNull
	public String getId(){
		return "general";
	}
	
	@Override
	@NotNull
	public String getPath(){
		return "bot/general";
	}
	
	@Override
	@NotNull
	public CompletableFuture<?> executeGuild(@NotNull SlashCommandInteraction event, @NotNull Guild guild, @NotNull Member member){
		return execute(event);
	}
	
	@Override
	@NotNull
	public CompletableFuture<?> executeUser(@NotNull SlashCommandInteraction event){
		return execute(event);
	}
	
	@NotNull
	private CompletableFuture<Message> execute(@NotNull SlashCommandInteraction event){
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
	
	@NotNull
	private String durationToString(@NotNull Duration duration){
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
