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
public class MegaWarnTimeConfig extends ValueConfiguration
{
	@Override
	public void setValue(Guild guild, Object value)
	{
		try
		{
			super.setValue(guild, Double.parseDouble(value.toString()));
		}
		catch(Exception e)
		{
			Log.warning(e, "Error parsing config value for mega warn time");
		}
	}
	
	@Override
	public String getName()
	{
		return "megaWarnTime";
	}
}
