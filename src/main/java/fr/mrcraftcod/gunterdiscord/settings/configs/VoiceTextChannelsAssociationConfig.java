package fr.mrcraftcod.gunterdiscord.settings.configs;

import fr.mrcraftcod.gunterdiscord.settings.configurations.MapConfiguration;
import net.dv8tion.jda.core.entities.Guild;
import java.util.function.Function;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-08-01
 */
public class VoiceTextChannelsAssociationConfig extends MapConfiguration<Long, Long>{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	public VoiceTextChannelsAssociationConfig(final Guild guild){
		super(guild);
	}
	
	@Override
	protected Function<String, Long> getKeyParser(){
		return Long::parseLong;
	}
	
	@Override
	protected Function<String, Long> getValueParser(){
		return Long::parseLong;
	}
	
	@Override
	public String getName(){
		return "voiceTextChannelAssociation";
	}
}
