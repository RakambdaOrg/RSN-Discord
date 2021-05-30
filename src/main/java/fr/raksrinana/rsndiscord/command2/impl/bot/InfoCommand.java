package fr.raksrinana.rsndiscord.command2.impl.bot;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.command.CommandResult;
import fr.raksrinana.rsndiscord.command2.base.group.SubCommand;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Properties;
import static fr.raksrinana.rsndiscord.command.CommandResult.SUCCESS;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static fr.raksrinana.rsndiscord.utils.Utilities.durationToString;
import static java.awt.Color.GREEN;
import static java.time.Duration.between;
import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;

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
			Log.getLogger().warn("Error reading properties from {}", resource, e);
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
	public CommandResult execute(@NotNull SlashCommandEvent event){
		var guild = event.getGuild();
		var author = event.getUser();
		var now = ZonedDateTime.now();
		
		var embed = new EmbedBuilder().setAuthor(author.getName(), null, author.getAvatarUrl())
				.setColor(GREEN)
				.addField(translate(guild, "infos.version"), botVersion, false)
				.addField(translate(guild, "infos.commit"), commitId, false)
				.addField(translate(guild, "infos.time"), now.format(ISO_ZONED_DATE_TIME), false)
				.addField(translate(guild, "infos.last-boot"), Main.bootTime.format(ISO_ZONED_DATE_TIME), false)
				.addField(translate(guild, "infos.elapsed"), durationToString(between(Main.bootTime, now)), false)
				.build();
		
		JDAWrappers.replyCommand(event, embed).submit();
		return SUCCESS;
	}
}
