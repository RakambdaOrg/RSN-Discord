package fr.mrcraftcod.gunterdiscord.settings;

import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.entities.Guild;
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
	public void setValue(Guild guild, Object value)
	{
		try
		{
			super.setValue(guild, Long.parseLong(value.toString()));
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
	public TextChannel getTextChannel(Guild guild)
	{
		try
		{
			return guild.getJDA().getTextChannelById(getLong(guild));
		}
		catch(InvalidClassException | NoValueDefinedException e)
		{
			Log.error("Error getting channel from config", e);
		}
		return null;
	}
}
