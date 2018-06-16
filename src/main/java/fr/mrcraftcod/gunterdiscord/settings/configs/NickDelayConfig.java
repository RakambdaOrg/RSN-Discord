package fr.mrcraftcod.gunterdiscord.settings.configs;

import fr.mrcraftcod.gunterdiscord.settings.configurations.ValueConfiguration;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.entities.Guild;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class NickDelayConfig extends ValueConfiguration
{
	@Override
	public void setValue(Guild guild, Object value)
	{
		try
		{
			super.setValue(guild, Integer.parseInt(value.toString()));
		}
		catch(Exception e)
		{
			Log.warning(e, "Error parsing config value for nick delay");
		}
	}
	
	@Override
	public String getName()
	{
		return "nickDelay";
	}
}
