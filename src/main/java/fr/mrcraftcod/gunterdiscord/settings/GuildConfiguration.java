package fr.mrcraftcod.gunterdiscord.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.settings.newConfigs.AnilistConfiguration;
import java.util.Objects;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-06-23.
 *
 * @author Thomas Couchoud
 * @since 2019-06-23
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GuildConfiguration{
	@JsonProperty("guildId")
	private long guildId;
	@JsonProperty("anilist")
	private AnilistConfiguration anilistConfiguration = new AnilistConfiguration();
	
	GuildConfiguration(final long guildId){
		this.guildId = guildId;
	}
	
	public GuildConfiguration mapOldConf(){
		final var guild = Main.getJDA().getGuildById(this.guildId);
		this.anilistConfiguration.mapOldConf(Objects.requireNonNull(guild));
		return this;
	}
}
