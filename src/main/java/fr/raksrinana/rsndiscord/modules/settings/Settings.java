package fr.raksrinana.rsndiscord.modules.settings;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Settings{
	public static final String GENERAL_CONF_NAME = "general";
	private static final Object cleaningLock = new Object();
	private static final ObjectReader guildConfigurationReader;
	private static final ObjectWriter guildConfigurationWriter;
	private static final ObjectReader generalConfigurationReader;
	private static final Map<Long, GuildConfiguration> configurations = new ConcurrentHashMap<>();
	private static final ObjectWriter generalConfigurationWriter;
	private static GeneralConfiguration generalConfiguration;
	
	@NonNull
	public static GuildConfiguration get(@NonNull final Guild guild){
		return configurations.computeIfAbsent(guild.getIdLong(),
				guildId -> loadGuildConfiguration(guild)
						.orElseThrow(() -> new RuntimeException("Failed to load configuration for guild " + guild)));
	}
	
	@NonNull
	private static Optional<GuildConfiguration> loadGuildConfiguration(@NonNull final Guild guild){
		final var guildConfPath = getConfigPath(guild.getId());
		if(guildConfPath.toFile().exists()){
			try(final var fis = new FileInputStream(guildConfPath.toFile())){
				return Optional.ofNullable(guildConfigurationReader.readValue(fis));
			}
			catch(final IOException e){
				Log.getLogger(guild).error("Failed to read settings in {}", guildConfPath, e);
				return Optional.empty();
			}
		}
		return Optional.of(new GuildConfiguration(guild.getIdLong()));
	}
	
	@NonNull
	private static Path getConfigPath(final String name){
		return Main.getParameters().getSettingsPath().resolve(name + ".json");
	}
	
	public static void save(){
		configurations.forEach(Settings::saveGuildConfiguration);
		if(Objects.nonNull(generalConfiguration)){
			saveGeneralConfiguration(generalConfiguration);
		}
	}
	
	private static void saveGuildConfiguration(final long guildId, @NonNull final GuildConfiguration value){
		final var guildConfPath = getConfigPath(Long.toString(guildId));
		try{
			Files.createDirectories(guildConfPath.getParent());
			guildConfigurationWriter.writeValueAsString(value);
			guildConfigurationWriter.writeValue(guildConfPath.toFile(), value);
			Log.getLogger(null).info("Wrote settings to {}", guildConfPath);
		}
		catch(final IOException e){
			Log.getLogger(null).error("Failed to write settings to {}", guildConfPath, e);
		}
	}
	
	public static void close(){
		save();
	}
	
	private static void saveGeneralConfiguration(@NonNull final GeneralConfiguration value){
		final var guildConfPath = getConfigPath(GENERAL_CONF_NAME);
		try{
			Files.createDirectories(guildConfPath.getParent());
			generalConfigurationWriter.writeValueAsString(value);
			generalConfigurationWriter.writeValue(guildConfPath.toFile(), value);
			Log.getLogger(null).info("Wrote settings to {}", guildConfPath);
		}
		catch(final IOException e){
			Log.getLogger(null).error("Failed to write settings to {}", guildConfPath, e);
		}
	}
	
	public static void clean(@NonNull JDA jda){
		synchronized(cleaningLock){
			Log.getLogger(null).info("Cleaning settings");
			configurations.forEach((guildId, configuration) -> {
				var guild = jda.getGuildById(guildId);
				try{
					configuration.cleanFields(guild, "[root]");
				}
				catch(Exception e){
					Log.getLogger(guild).error("Failed to clean guild configuration", e);
				}
			});
			if(Objects.nonNull(generalConfiguration)){
				try{
					generalConfiguration.cleanFields(null, "[root]");
				}
				catch(Exception e){
					Log.getLogger(null).error("Failed to clean guild configuration", e);
				}
			}
			Log.getLogger(null).info("Done cleaning settings");
		}
	}
	
	@NonNull
	public static GeneralConfiguration getGeneral(){
		if(Objects.isNull(generalConfiguration)){
			generalConfiguration = loadGeneralConfiguration()
					.orElseThrow(() -> new RuntimeException("Failed to load general configuration "));
		}
		return generalConfiguration;
	}
	
	@NonNull
	private static Optional<GeneralConfiguration> loadGeneralConfiguration(){
		final var generalConfPath = getConfigPath(GENERAL_CONF_NAME);
		if(generalConfPath.toFile().exists()){
			try(final var fis = new FileInputStream(generalConfPath.toFile())){
				return Optional.ofNullable(generalConfigurationReader.readValue(fis));
			}
			catch(final IOException e){
				Log.getLogger(null).error("Failed to read settings in {}", generalConfPath, e);
				return Optional.empty();
			}
		}
		return Optional.of(new GeneralConfiguration());
	}
	
	static{
		final var mapper = new ObjectMapper();
		mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker().withFieldVisibility(JsonAutoDetect.Visibility.ANY).withGetterVisibility(JsonAutoDetect.Visibility.NONE).withSetterVisibility(JsonAutoDetect.Visibility.NONE).withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		guildConfigurationReader = mapper.readerFor(GuildConfiguration.class);
		guildConfigurationWriter = mapper.writerFor(GuildConfiguration.class);
		generalConfigurationReader = mapper.readerFor(GeneralConfiguration.class);
		generalConfigurationWriter = mapper.writerFor(GeneralConfiguration.class);
	}
}
