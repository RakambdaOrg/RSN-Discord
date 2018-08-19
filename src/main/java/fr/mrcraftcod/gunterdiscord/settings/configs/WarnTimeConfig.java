package fr.mrcraftcod.gunterdiscord.settings.configs;

import fr.mrcraftcod.gunterdiscord.settings.configurations.DoubleValueConfiguration;
import net.dv8tion.jda.core.entities.Guild;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class WarnTimeConfig extends DoubleValueConfiguration{
	
	/**
	 * Constructor.
	 *
	 * @param guild The guild for this config.
	 */
	public WarnTimeConfig(final Guild guild){
		super(guild);
	}
	
	@Override
	public String getName(){
		return "warnTime";
	}
}
