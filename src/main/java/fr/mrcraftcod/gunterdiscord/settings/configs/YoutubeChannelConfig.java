package fr.mrcraftcod.gunterdiscord.settings.configs;

import fr.mrcraftcod.gunterdiscord.settings.configurations.MapConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import java.util.function.Function;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class YoutubeChannelConfig extends MapConfiguration<Long, String>{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	public YoutubeChannelConfig(final Guild guild){
		super(guild);
	}
	
	@Override
	protected Function<String, Long> getKeyParser(){
		return Long::parseLong;
	}
	
	@Override
	protected Function<String, String> getValueParser(){
		return s -> s;
	}
	
	@Override
	public String getName(){
		return "youtubeChannel";
	}
}
