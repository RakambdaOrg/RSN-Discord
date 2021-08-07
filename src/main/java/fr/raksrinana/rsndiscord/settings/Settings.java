package fr.raksrinana.rsndiscord.settings;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.settings.impl.GeneralConfiguration;
import fr.raksrinana.rsndiscord.settings.impl.GuildConfiguration;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

@Log4j2
public class Settings{
	public static final String GENERAL_CONF_NAME = "general";
	private static final Object cleaningLock = new Object();
	private static final ObjectReader guildConfigurationReader;
	private static final ObjectWriter guildConfigurationWriter;
	private static final ObjectReader generalConfigurationReader;
	private static final Map<Long, GuildConfiguration> configurations = new ConcurrentHashMap<>();
	private static final ObjectWriter generalConfigurationWriter;
	private static GeneralConfiguration generalConfiguration;
	
	@NotNull
	public static GuildConfiguration get(@NotNull Guild guild){
		return configurations.computeIfAbsent(guild.getIdLong(), guildId -> loadGuildConfiguration(guild)
				.orElseThrow(() -> new RuntimeException("Failed to load configuration for guild " + guild)));
	}
	
	@NotNull
	private static Optional<GuildConfiguration> loadGuildConfiguration(@NotNull Guild guild){
		var guildConfPath = getConfigPath(guild.getId());
		if(guildConfPath.toFile().exists()){
			try(var fis = new FileInputStream(guildConfPath.toFile())){
				return ofNullable(guildConfigurationReader.readValue(fis));
			}
			catch(IOException e){
				log.error("Failed to read settings in {}", guildConfPath, e);
				return Optional.empty();
			}
		}
		return Optional.of(new GuildConfiguration(guild.getIdLong()));
	}
	
	@NotNull
	private static Path getConfigPath(@NotNull String name){
		return Main.getParameters().getSettingsPath().resolve(name + ".json");
	}
	
	public static void save(){
		configurations.forEach(Settings::saveGuildConfiguration);
		if(nonNull(generalConfiguration)){
			saveGeneralConfiguration(generalConfiguration);
		}
	}
	
	private static void saveGuildConfiguration(long guildId, @NotNull GuildConfiguration value){
		var guildConfPath = getConfigPath(Long.toString(guildId));
		try{
			Files.createDirectories(guildConfPath.getParent());
			guildConfigurationWriter.writeValueAsString(value);
			guildConfigurationWriter.writeValue(guildConfPath.toFile(), value);
			log.info("Wrote settings to {}", guildConfPath);
		}
		catch(IOException e){
			log.error("Failed to write settings to {}", guildConfPath, e);
		}
	}
	
	public static void close(){
		save();
	}
	
	private static void saveGeneralConfiguration(@NotNull GeneralConfiguration value){
		var guildConfPath = getConfigPath(GENERAL_CONF_NAME);
		try{
			Files.createDirectories(guildConfPath.getParent());
			generalConfigurationWriter.writeValueAsString(value);
			generalConfigurationWriter.writeValue(guildConfPath.toFile(), value);
			log.info("Wrote settings to {}", guildConfPath);
		}
		catch(IOException e){
			log.error("Failed to write settings to {}", guildConfPath, e);
		}
	}
	
	public static void clean(@NotNull JDA jda){
		synchronized(cleaningLock){
			log.info("Cleaning settings");
			configurations.forEach((guildId, configuration) -> {
				var guild = jda.getGuildById(guildId);
				try{
					configuration.cleanFields(guild, "[root]");
				}
				catch(Exception e){
					log.error("Failed to clean guild configuration", e);
				}
			});
			if(nonNull(generalConfiguration)){
				try{
					generalConfiguration.cleanFields(null, "[root]");
				}
				catch(Exception e){
					log.error("Failed to clean guild configuration", e);
				}
			}
			log.info("Done cleaning settings");
		}
	}
	
	@NotNull
	public static GeneralConfiguration getGeneral(){
		if(Objects.isNull(generalConfiguration)){
			generalConfiguration = loadGeneralConfiguration()
					.orElseThrow(() -> new RuntimeException("Failed to load general configuration "));
		}
		return generalConfiguration;
	}
	
	@NotNull
	private static Optional<GeneralConfiguration> loadGeneralConfiguration(){
		var generalConfPath = getConfigPath(GENERAL_CONF_NAME);
		if(generalConfPath.toFile().exists()){
			try(var fis = new FileInputStream(generalConfPath.toFile())){
				return ofNullable(generalConfigurationReader.readValue(fis));
			}
			catch(IOException e){
				log.error("Failed to read settings in {}", generalConfPath, e);
				return Optional.empty();
			}
		}
		return Optional.of(new GeneralConfiguration());
	}
	
	static{
		var mapper = new ObjectMapper();
		mapper.setVisibility(mapper.getSerializationConfig()
				.getDefaultVisibilityChecker()
				.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
				.withGetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withSetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		guildConfigurationReader = mapper.readerFor(GuildConfiguration.class);
		guildConfigurationWriter = mapper.writerFor(GuildConfiguration.class);
		generalConfigurationReader = mapper.readerFor(GeneralConfiguration.class);
		generalConfigurationWriter = mapper.writerFor(GeneralConfiguration.class);
	}
}
