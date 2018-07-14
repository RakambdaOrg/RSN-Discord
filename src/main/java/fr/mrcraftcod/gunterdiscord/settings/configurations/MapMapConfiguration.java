package fr.mrcraftcod.gunterdiscord.settings.configurations;

import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCommand;
import fr.mrcraftcod.gunterdiscord.settings.Configuration;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import net.dv8tion.jda.core.entities.Guild;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public abstract class MapMapConfiguration<K, V, W> extends Configuration
{
	private Map<K, Map<V, W>> lastValue = null;
	
	/**
	 * Get the list of values from the given key.
	 *
	 * @param key The key to get.
	 *
	 * @return The values or null if not found.
	 */
	public Map<V, W> getValue(Guild guild, K key)
	{
		return getAsMap(guild).get(key);
	}
	
	/**
	 * Get the map of this configuration.
	 *
	 * @param guild The guild.
	 *
	 * @return The map.
	 *
	 * @throws IllegalArgumentException If this configuration isn't a map.
	 */
	public Map<K, Map<V, W>> getAsMap(Guild guild) throws IllegalArgumentException
	{
		if(lastValue != null)
			return lastValue;
		Map<K, Map<V, W>> elements = new HashMap<>();
		JSONObject map = getObjectMap(guild);
		if(map == null)
			Settings.resetMap(guild, this);
		else
			for(String key: map.keySet())
			{
				K kKey = getKeyParser().apply(key);
				JSONObject value = map.optJSONObject(key);
				if(value != null)
					elements.put(kKey, value.keySet().stream().collect(Collectors.toMap(k -> getKeyValueParser().apply(k), k -> getValueParser().apply(value.get(k).toString()))));
			}
		return elements;
	}
	
	/**
	 * Get the JSON Object.
	 *
	 * @param guild The guild.
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
	
	/**
	 * Get the parser to parse back string keys to K.
	 *
	 * @return The parser.
	 */
	protected abstract Function<String, K> getKeyParser();
	
	/**
	 * Get the parser to parse back key string values to W.
	 *
	 * @return The parser.
	 */
	protected abstract Function<String, V> getKeyValueParser();
	
	/**
	 * Get the parser to parse back string values to W.
	 *
	 * @return The parser.
	 */
	protected abstract Function<String, W> getValueParser();
	
	/**
	 * Add a value to the map list.
	 *
	 * @param guild       The guild.
	 * @param key         The key to add into.
	 * @param value       The second key key.
	 * @param insideValue The value inside the second map.
	 */
	public void addValue(Guild guild, K key, V value, W insideValue)
	{
		if(value == null || insideValue == null)
			addValue(guild, key);
		else
		{
			if(lastValue != null)
			{
				if(!lastValue.containsKey(key))
					lastValue.put(key, new HashMap<>());
				lastValue.get(key).put(value, insideValue);
			}
			Settings.mapMapValue(guild, this, key, value, insideValue);
		}
	}
	
	/**
	 * Add an empty value to the map list.
	 *
	 * @param guild The guild.
	 * @param key   The key to add into.
	 */
	public void addValue(Guild guild, K key)
	{
		if(lastValue != null && !lastValue.containsKey(key))
		{
			lastValue.put(key, new HashMap<>());
		}
		Settings.mapMapValue(guild, this, key);
	}
	
	/**
	 * Delete a value inside a key.
	 *
	 * @param guild The guild.
	 * @param key   The key.
	 * @param value The value.
	 */
	public void deleteKeyValue(Guild guild, K key, V value)
	{
		if(value == null)
			deleteKey(guild, key);
		else
		{
			if(lastValue != null && lastValue.containsKey(key))
				lastValue.get(key).remove(value);
			Settings.deleteKey(guild, this, key, value, getMatcher());
		}
	}
	
	/**
	 * Delete the key.
	 *
	 * @param guild The guild.
	 * @param key   The key.
	 */
	public void deleteKey(Guild guild, K key)
	{
		if(lastValue != null)
			lastValue.remove(key);
		Settings.deleteKey(guild, this, key);
	}
	
	/**
	 * Get the matcher to declare objects equals. This is used when deleting a key value.
	 *
	 * @return The matcher.
	 */
	protected BiFunction<Object, V, Boolean> getMatcher()
	{
		return Objects::equals;
	}
	
	@Override
	public boolean isActionAllowed(ConfigurationCommand.ChangeConfigType action)
	{
		return action == ConfigurationCommand.ChangeConfigType.ADD || action == ConfigurationCommand.ChangeConfigType.REMOVE || action == ConfigurationCommand.ChangeConfigType.SHOW;
	}
	
	@Override
	public ConfigType getType()
	{
		return ConfigType.MAP;
	}
}
