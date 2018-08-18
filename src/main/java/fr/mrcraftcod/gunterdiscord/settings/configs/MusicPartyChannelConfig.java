package fr.mrcraftcod.gunterdiscord.settings.configs;

import fr.mrcraftcod.gunterdiscord.settings.configurations.SingleChannelConfiguration;
import net.dv8tion.jda.core.entities.Guild;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-21
 */
public class MusicPartyChannelConfig extends SingleChannelConfiguration{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	public MusicPartyChannelConfig(final Guild guild){
		super(guild);
	}
	
	@Override
	public String getName(){
		return "musicPartyChannel";
	}
}
