package fr.mrcraftcod.gunterdiscord.settings;

import fr.mrcraftcod.gunterdiscord.commands.SetConfigCommand;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public abstract class MapListConfiguration<K, V> extends Configuration
{
	private Map<K, List<V>> lastValue = null;
	
	/**
	 * Get the list of values from the given key.
	 *
	 * @param key The key to get.
	 *
	 * @return The values or null if not found.
	 */
	public List<V> getValue(K key)
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
	 * Add a value to the map list.
	 *
	 * @param key   The key to add into.
	 * @param value The value to add at the key.
	 */
	public void addValue(K key, V value)
	{
		if(lastValue != null)
			lastValue.put(key, new ArrayList<>());
		Settings.mapListValue(this, key, value);
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
			if(lastValue != null)
				lastValue.remove(key);
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
	public Map<K, List<V>> getAsMap() throws IllegalArgumentException
	{
		if(lastValue != null)
			return lastValue;
		@SuppressWarnings("unchecked") Class<V> klassV = (Class<V>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
		Map<K, List<V>> elements = new HashMap<>();
		JSONObject map = getObjectMap();
		if(map == null)
			Settings.resetMap(this);
		else
			for(String key : map.keySet())
			{
				K kKey = getKeyParser().apply(key);
				if(!elements.containsKey(kKey))
					elements.put(kKey, new ArrayList<>());
				JSONArray value = map.optJSONArray(key);
				if(value != null)
					value.toList().stream().map(klassV::cast).forEach(o -> elements.get(kKey).add(o));
			}
		return elements;
	}
	
	@Override
	public boolean isActionAllowed(SetConfigCommand.ChangeConfigType action)
	{
		return action == SetConfigCommand.ChangeConfigType.ADD || action == SetConfigCommand.ChangeConfigType.REMOVE || action == SetConfigCommand.ChangeConfigType.SHOW;
	}
}
