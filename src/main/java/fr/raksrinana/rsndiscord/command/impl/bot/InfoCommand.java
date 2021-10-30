package fr.raksrinana.rsndiscord.command.impl.bot;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command.base.group.SubCommand;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Properties;
import static fr.raksrinana.rsndiscord.command.CommandResult.HANDLED;
import static fr.raksrinana.rsndiscord.utils.Utilities.durationToString;
import static java.awt.Color.GREEN;
import static java.time.Duration.between;
import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;

@Log4j2
public class InfoCommand extends SubCommand{
	private String botVersion;
	private String commitId;
	
	public InfoCommand(){
		loadAllProperties();
	}
	
	private void loadAllProperties(){
		var versionProperties = loadProperties("/version.properties");
		botVersion = versionProperties.getProperty("bot.version", "Unknown");
		
		var gitProperties = loadProperties("/git.properties");
		commitId = gitProperties.getProperty("git.commit.id", "Unknown");
	}
	
	@NotNull
	private Properties loadProperties(@NotNull String resource){
		var properties = new Properties();
		try{
			var versionPropertiesFile = Main.class.getResource(resource);
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
		return "info";
	}
	
	@Override
	@NotNull
	public String getShortDescription(){
		return "Get generic infos about the bot";
	}
	
	@Override
	@NotNull
	public CommandResult executeGuild(@NotNull SlashCommandEvent event, @NotNull Guild guild, @NotNull Member member){
		return execute(event);
	}
	
	@Override
	@NotNull
	public CommandResult executeUser(@NotNull SlashCommandEvent event){
		return execute(event);
	}
	
	@NotNull
	private CommandResult execute(@NotNull SlashCommandEvent event){
		var now = ZonedDateTime.now();
		
		var embed = new EmbedBuilder()
				.setColor(GREEN)
				.addField("Bot version", botVersion, false)
				.addField("Commit id", commitId, false)
				.addField("Current time", now.format(ISO_ZONED_DATE_TIME), false)
				.addField("Last start", Main.bootTime.format(ISO_ZONED_DATE_TIME), false)
				.addField("Time elapsed", durationToString(between(Main.bootTime, now)), false)
				.build();
		
		JDAWrappers.edit(event, embed).submit();
		return HANDLED;
	}
}
