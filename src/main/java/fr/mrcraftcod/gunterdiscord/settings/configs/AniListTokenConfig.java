package fr.mrcraftcod.gunterdiscord.settings.configs;

import fr.mrcraftcod.gunterdiscord.settings.configurations.MapConfiguration;
import net.dv8tion.jda.core.entities.Guild;
import java.util.function.Function;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-08.
 *
 * @author Thomas Couchoud
 * @since 2018-10-08
 */
public class AniListTokenConfig extends MapConfiguration<Long, String>{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	public AniListTokenConfig(final Guild guild){
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
		return "nickLastChange";
	}
}
