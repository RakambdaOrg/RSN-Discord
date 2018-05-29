package fr.mrcraftcod.gunterdiscord.settings.configurations;

import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.settings.Configuration;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import net.dv8tion.jda.core.entities.Guild;
import org.json.JSONArray;
import java.io.InvalidClassException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public abstract class ListConfiguration<T> extends Configuration
{
	private List<T> lastValue = null;
	
	/**
	 * Add a value to the list.
	 *
	 * @param guild The guild.
	 * @param value The value to add.
	 */
	public void addValue(Guild guild, T value)
	{
		if(lastValue != null)
			lastValue.add(value);
		Settings.addValue(guild, this, value);
	}
	
	/**
	 * Remove a value from the list.
	 *
	 * @param guild The guild.
	 * @param value The value to remove.
	 */
	public void removeValue(Guild guild, T value)
	{
		if(lastValue != null)
			lastValue.remove(value);
		Settings.removeValue(guild, this, value);
	}
	
	@Override
	public ConfigType getType()
	{
		return ConfigType.LIST;
	}
	
	/**
	 * Get a list of the values.
	 *
	 * @param guild The guild.
	 *
	 * @return The values list.
	 *
	 * @throws IllegalArgumentException If the configuration isn't a list.
	 * @throws InvalidClassException    If the values are not of type T.
	 */
	public List<T> getAsList(Guild guild) throws IllegalArgumentException, InvalidClassException
	{
		if(lastValue != null)
			return lastValue;
		Type type = getParameterizedType(getClass());
		if(type instanceof ParameterizedType)
		{
			@SuppressWarnings("unchecked") Class<T> klass = (Class<T>) ((ParameterizedType) type).getActualTypeArguments()[0];
			List<T> elements = new LinkedList<>();
			JSONArray array = getObjectList(guild);
			if(array == null)
				Settings.resetList(guild, this);
			else
				for(int i = 0; i < array.length(); i++)
				{
					Object value = array.get(i);
					if(klass.isInstance(value))
						elements.add(klass.cast(value));
					else
						throw new InvalidClassException("Config is not a T");
				}
			return elements;
		}
		throw new InvalidClassException("Failed to get parameterized type");
	}
	
	/**
	 * Get the JSON array.
	 *
	 * @param guild The guild.
	 *
	 * @return The JSON array.
	 *
	 * @throws IllegalArgumentException If the configuration isn't a list.
	 */
	private JSONArray getObjectList(Guild guild) throws IllegalArgumentException
	{
		if(getType() != ConfigType.LIST)
			throw new IllegalArgumentException("Not a list config");
		return Settings.getArray(guild, getName());
	}
	
	private Type getParameterizedType(Class klass)
	{
		if(klass == null || klass.equals(Object.class))
			return klass;
		if(klass.getGenericSuperclass() instanceof ParameterizedType)
			return klass.getGenericSuperclass();
		return getParameterizedType(klass.getSuperclass());
	}
	
	@Override
	public boolean isActionAllowed(ConfigurationCommand.ChangeConfigType action)
	{
		return action == ConfigurationCommand.ChangeConfigType.ADD || action == ConfigurationCommand.ChangeConfigType.REMOVE || action == ConfigurationCommand.ChangeConfigType.SHOW;
	}
}
