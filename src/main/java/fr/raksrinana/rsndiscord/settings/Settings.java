package fr.raksrinana.rsndiscord.settings;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Settings{
	private static final ObjectReader objectReader;
	private static final ObjectWriter objectWriter;
	private static final Map<Long, GuildConfiguration> configurations = new ConcurrentHashMap<>();
	
	@NonNull
	public static GuildConfiguration get(@NonNull final Guild guild){
		return configurations.computeIfAbsent(guild.getIdLong(), guildId -> loadConfiguration(guild).orElse(new GuildConfiguration(guildId)));
	}
	
	@NonNull
	private static Optional<GuildConfiguration> loadConfiguration(@NonNull final Guild guild){
		final var guildConfPath = getConfigPath(guild.getIdLong());
		if(guildConfPath.toFile().exists()){
			try(final var fis = new FileInputStream(guildConfPath.toFile())){
				return Optional.ofNullable(objectReader.readValue(fis));
			}
			catch(final IOException e){
				Log.getLogger(guild).error("Failed to read settings in {}", guildConfPath, e);
			}
		}
		return Optional.empty();
	}
	
	@NonNull
	private static Path getConfigPath(final long guildId){
		return Main.getParameters().getSettingsPath().resolve(guildId + ".json");
	}
	
	public static void close(){
		save();
	}
	
	public static void save(){
		configurations.forEach(Settings::saveConfiguration);
	}
	
	private static void saveConfiguration(final long guildId, @NonNull final GuildConfiguration value){
		final var guildConfPath = getConfigPath(guildId);
		guildConfPath.getParent().toFile().mkdirs();
		try{
			objectWriter.writeValueAsString(value);
			objectWriter.writeValue(guildConfPath.toFile(), value);
			Log.getLogger(null).info("Wrote settings to {}", guildConfPath);
		}
		catch(final IOException e){
			Log.getLogger(null).error("Failed to write settings to {}", guildConfPath, e);
		}
	}
	
	public static void clean(){
		Log.getLogger(null).info("Cleaning settings");
		configurations.values().forEach(guildConfiguration -> {
			try{
				guildConfiguration.cleanFields();
			}
			catch(Exception e){
				Log.getLogger(null).error("Failed to clean guild configuration", e);
			}
		});
		Log.getLogger(null).info("Done cleaning settings");
	}
	
	static{
		final var mapper = new ObjectMapper();
		mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker().withFieldVisibility(JsonAutoDetect.Visibility.ANY).withGetterVisibility(JsonAutoDetect.Visibility.NONE).withSetterVisibility(JsonAutoDetect.Visibility.NONE).withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectReader = mapper.readerFor(GuildConfiguration.class);
		objectWriter = mapper.writerFor(GuildConfiguration.class);
	}
}
