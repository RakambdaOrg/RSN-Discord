package fr.mrcraftcod.gunterdiscord.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import fr.mrcraftcod.gunterdiscord.Main;
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
public class NewSettings implements AutoCloseable{
	private static final ObjectReader objectReader = new ObjectMapper().readerFor(GuildConfiguration.class);
	private static final ObjectWriter objectWriter = new ObjectMapper().writerFor(GuildConfiguration.class);
	private static final Map<Long, GuildConfiguration> configurations = new HashMap<>();
	
	@Nonnull
	public static GuildConfiguration getConfiguration(@Nonnull Guild guild){
		return configurations.computeIfAbsent(guild.getIdLong(), guildId -> loadConfiguration(guildId).orElse(new GuildConfiguration(guildId)));
	}
	
	@Nonnull
	private static Optional<GuildConfiguration> loadConfiguration(long guildId){
		final var guildConfPath = getConfigPath(guildId);
		if(guildConfPath.toFile().exists()){
			try(FileInputStream fis = new FileInputStream(guildConfPath.toFile())){
				return Optional.ofNullable(objectReader.readValue(fis));
			}
			catch(IOException e){
				Log.getLogger(guildId).error("Failed to read settings in {}", guildConfPath, e);
			}
		}
		return Settings.getServerSettings(Main.getJDA().getGuildById(guildId)).map(oldConf -> new GuildConfiguration(guildId).mapOldConf());
	}
	
	@Nonnull
	private static Path getConfigPath(long guildId){
		return Paths.get("settings", guildId + ".json");
	}
	
	@Override
	public void close(){
		configurations.forEach(this::saveConfiguration);
	}
	
	private void saveConfiguration(Long guildId, GuildConfiguration value){
		final var guildConfPath = getConfigPath(guildId);
		try{
			objectWriter.writeValue(guildConfPath.toFile(), value);
			Log.getLogger(guildId).info("Wrote settings to {}", guildConfPath);
		}
		catch(IOException e){
			Log.getLogger(guildId).error("Failed to write settings to {}", guildConfPath, e);
		}
	}
}
