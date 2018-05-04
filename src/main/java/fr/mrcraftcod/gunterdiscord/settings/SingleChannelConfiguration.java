package fr.mrcraftcod.gunterdiscord.settings;

import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;
import java.io.InvalidClassException;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-04
 */
public abstract class SingleChannelConfiguration extends ValueConfiguration
{
	@Override
	public void setValue(Object value)
	{
		try
		{
			super.setValue(Long.parseLong(value.toString()));
		}
		catch(Exception e)
		{
			Log.warning("Error parsing config value for channel ID", e);
		}
	}
	
	/**
	 * Get the text channel.
	 *
	 * @param jda The JDA.
	 *
	 * @return The text channel or null if not found.
	 */
	public TextChannel getTextChannel(JDA jda)
	{
		try
		{
			return jda.getTextChannelById(getLong());
		}
		catch(InvalidClassException | NoValueDefinedException e)
		{
			Log.error("Error getting channel from config", e);
		}
		return null;
	}
}
