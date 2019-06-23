package fr.mrcraftcod.gunterdiscord.settings.configs;

import fr.mrcraftcod.gunterdiscord.settings.configurations.MultipleChannelConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class NoXPChannelsConfig extends MultipleChannelConfiguration{
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	public NoXPChannelsConfig(@Nullable final Guild guild){
		super(guild);
	}
	
	@Nonnull
	@Override
	public String getName(){
		return "noXPChannels";
	}
}
