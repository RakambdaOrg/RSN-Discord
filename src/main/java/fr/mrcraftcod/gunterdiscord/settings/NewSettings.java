package fr.mrcraftcod.gunterdiscord.settings;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import fr.mrcraftcod.gunterdiscord.utils.log.Log;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-06-23.
 *
 * @author Thomas Couchoud
 * @since 2019-06-23
 */
public class NewSettings{
	private static final ObjectReader objectReader;
	private static final ObjectWriter objectWriter;
	private static final Map<Long, GuildConfiguration> configurations = new HashMap<>();
	
	@Nonnull
	public static GuildConfiguration getConfiguration(@Nonnull final Guild guild){
		return configurations.computeIfAbsent(guild.getIdLong(), guildId -> loadConfiguration(guildId).orElse(new GuildConfiguration(guildId)));
	}
	
	@Nonnull
	private static Optional<GuildConfiguration> loadConfiguration(final long guildId){
		final var guildConfPath = getConfigPath(guildId);
		if(guildConfPath.toFile().exists()){
			try(final var fis = new FileInputStream(guildConfPath.toFile())){
				return Optional.ofNullable(objectReader.readValue(fis));
			}
			catch(final IOException e){
				Log.getLogger(guildId).error("Failed to read settings in {}", guildConfPath, e);
			}
		}
		return Optional.empty();
	}
	
	@Nonnull
	private static Path getConfigPath(final long guildId){
		return Paths.get("settings", guildId + ".json");
	}
	
	public static void close(){
		configurations.forEach(NewSettings::saveConfiguration);
	}
	
	private static void saveConfiguration(final long guildId, @Nonnull final GuildConfiguration value){
		final var guildConfPath = getConfigPath(guildId);
		guildConfPath.getParent().toFile().mkdirs();
		try{
			objectWriter.writeValue(guildConfPath.toFile(), value);
			Log.getLogger(guildId).info("Wrote settings to {}", guildConfPath);
		}
		catch(final IOException e){
			Log.getLogger(guildId).error("Failed to write settings to {}", guildConfPath, e);
		}
	}
	
	static{
		final var mapper = new ObjectMapper();
		mapper.setVisibility(mapper.getSerializationConfig().getDefaultVisibilityChecker().withFieldVisibility(JsonAutoDetect.Visibility.ANY).withGetterVisibility(JsonAutoDetect.Visibility.NONE).withSetterVisibility(JsonAutoDetect.Visibility.NONE).withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
		objectReader = mapper.readerFor(GuildConfiguration.class);
		objectWriter = mapper.writerFor(GuildConfiguration.class);
	}
}
