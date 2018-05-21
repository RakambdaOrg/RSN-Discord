package fr.mrcraftcod.gunterdiscord.settings;

import fr.mrcraftcod.gunterdiscord.commands.SetConfigCommand;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.entities.Guild;
import org.json.JSONObject;
import java.io.InvalidClassException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public abstract class MapConfiguration<K, V> extends Configuration
{
	private Map<K, V> lastValue = null;
	
	/**
	 * Get the value from the given key.
	 *
	 * @param key The key to get.
	 *
	 * @return The value or null if not found.
	 */
	public V getValue(Guild guild, K key)
	{
		try
		{
			return getAsMap(guild).get(key);
		}
		catch(InvalidClassException e)
		{
			Log.error("Can't get value " + getName() + " with key " + key, e);
		}
		return null;
	}
	
	/**
	 * Get the parser to parse back string keys to K.
	 *
	 * @return The parser.
	 */
	abstract Function<String, K> getKeyParser();
	
	/**
	 * Get the map of this configuration.
	 *
	 * @return The map.
	 *
	 * @throws IllegalArgumentException If this configuration isn't a map.
	 * @throws InvalidClassException    If either they key isn't K or the value isn't V.
	 */
	public Map<K, V> getAsMap(Guild guild) throws IllegalArgumentException, InvalidClassException
	{
		if(lastValue != null)
			return lastValue;
		@SuppressWarnings("unchecked")
		Class<V> klassV = (Class<V>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
		Map<K, V> elements = new HashMap<>();
		JSONObject map = getObjectMap(guild);
		if(map == null)
			Settings.resetMap(guild, this);
		else
			for(String key : map.keySet())
			{
				Object value = map.get(key);
				if(klassV.isInstance(value))
					elements.put(getKeyParser().apply(key), klassV.cast(value));
				else
					throw new InvalidClassException("Config is not a K,V");
			}
		return elements;
	}
	
	/**
	 * Get the JSON Object.
	 *
	 * @return The JSON object.
	 *
	 * @throws IllegalArgumentException If this configuration isn't a map.
	 */
	private JSONObject getObjectMap(Guild guild) throws IllegalArgumentException
	{
		if(getType() != ConfigType.MAP)
			throw new IllegalArgumentException("Not a map config");
		return Settings.getJSONObject(guild, getName());
	}
	
	@Override
	public ConfigType getType()
	{
		return ConfigType.MAP;
	}
	
	/**
	 * Add a value to the map.
	 *
	 * @param key   The key to add into.
	 * @param value The value to set at the key.
	 */
	public void addValue(Guild guild, K key, V value)
	{
		if(lastValue != null)
			lastValue.put(key, value);
		Settings.mapValue(guild, this, key, value);
	}
	
	/**
	 * Delete the key.
	 *
	 * @param key The key.
	 */
	public void deleteKey(Guild guild, K key)
	{
		if(lastValue != null)
			lastValue.remove(key);
		Settings.deleteKey(guild, this, key);
	}
	
	@Override
	public boolean isActionAllowed(SetConfigCommand.ChangeConfigType action)
	{
		return action == SetConfigCommand.ChangeConfigType.ADD || action == SetConfigCommand.ChangeConfigType.REMOVE || action == SetConfigCommand.ChangeConfigType.SHOW;
	}
}
