package fr.mrcraftcod.gunterdiscord.settings;

import fr.mrcraftcod.gunterdiscord.commands.SetConfigCommand;
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
	public Map<V, W> getValue(K key)
	{
		return getAsMap().get(key);
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
	 * Add an empty value to the map list.
	 *
	 * @param key   The key to add into.
	 */
	public void addValue(K key)
	{
		if(lastValue != null && !lastValue.containsKey(key))
		{
			lastValue.put(key, new HashMap<>());
		}
		Settings.mapMapValue(this, key);
	}
	
	/**
	 * Add a value to the map list.
	 *
	 * @param key   The key to add into.
	 * @param value The value to add at the key.
	 */
	public void addValue(K key, V value, W insideValue)
	{
		if(value == null || insideValue == null)
			addValue(key);
		else
		{
			if(lastValue != null)
			{
				if(!lastValue.containsKey(key))
					lastValue.put(key, new HashMap<>());
				lastValue.get(key).put(value, insideValue);
			}
			Settings.mapMapValue(this, key, value, insideValue);
		}
	}
	
	/**
	 * Delete the key.
	 *
	 * @param key The key.
	 */
	public void deleteKey(K key)
	{
		if(lastValue != null)
			lastValue.remove(key);
		Settings.deleteKey(this, key);
	}
	
	/**
	 * Delete a value inside a key.
	 *
	 * @param key   The key.
	 * @param value The value.
	 */
	public void deleteKeyValue(K key, V value)
	{
		if(value == null)
			deleteKey(key);
		else
		{
			if(lastValue != null && lastValue.containsKey(key))
				lastValue.get(key).remove(value);
			Settings.deleteKey(this, key, value, getMatcher());
		}
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
	public ConfigType getType()
	{
		return ConfigType.MAP;
	}
	
	/**
	 * Get the JSON Object.
	 *
	 * @return The JSON object.
	 *
	 * @throws IllegalArgumentException If this configuration isn't a map.
	 */
	private JSONObject getObjectMap() throws IllegalArgumentException
	{
		if(getType() != ConfigType.MAP)
			throw new IllegalArgumentException("Not a map config");
		return Settings.getJSONObject(getName());
	}
	
	/**
	 * Get the map of this configuration.
	 *
	 * @return The map.
	 *
	 * @throws IllegalArgumentException If this configuration isn't a map.
	 */
	public Map<K, Map<V, W>> getAsMap() throws IllegalArgumentException
	{
		if(lastValue != null)
			return lastValue;
		Map<K, Map<V, W>> elements = new HashMap<>();
		JSONObject map = getObjectMap();
		if(map == null)
			Settings.resetMap(this);
		else
			for(String key : map.keySet())
			{
				K kKey = getKeyParser().apply(key);
				JSONObject value = map.optJSONObject(key);
				if(value != null)
					elements.put(kKey, value.keySet().stream().collect(Collectors.toMap(k -> getKeyValueParser().apply(k), k -> getValueParser().apply(value.getString(k)))));
			}
		return elements;
	}
	
	@Override
	public boolean isActionAllowed(SetConfigCommand.ChangeConfigType action)
	{
		return action == SetConfigCommand.ChangeConfigType.ADD || action == SetConfigCommand.ChangeConfigType.REMOVE || action == SetConfigCommand.ChangeConfigType.SHOW;
	}
}
