package fr.mrcraftcod.gunterdiscord.settings;

import fr.mrcraftcod.gunterdiscord.commands.SetConfigCommand;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InvalidClassException;
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
	
	public List<V> getValue(K key)
	{
		try
		{
			return getAsMap().get(key);
		}
		catch(NoValueDefinedException | InvalidClassException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	protected abstract Function<String, K> getKeyParser();
	
	public void addValue(K key, V value)
	{
		if(lastValue != null)
			lastValue.put(key, new ArrayList<>());
		Settings.mapListValue(this, key, value);
	}
	
	public void deleteKey(K key)
	{
		if(lastValue != null)
			lastValue.remove(key);
		Settings.deleteKey(this, key);
	}
	
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
	
	protected BiFunction<Object, V, Boolean> getMatcher()
	{
		return Objects::equals;
	}
	
	@Override
	public ConfigType getType()
	{
		return ConfigType.MAP;
	}
	
	private static JSONObject getObjectMap(Configuration configuration) throws IllegalArgumentException
	{
		if(configuration.getType() != ConfigType.MAP)
			throw new IllegalArgumentException("Not a map config");
		return Settings.getJSONObject(configuration.getName());
	}
	
	public Map<K, List<V>> getAsMap() throws NoValueDefinedException, IllegalArgumentException, InvalidClassException
	{
		if(lastValue != null)
			return lastValue;
		Class<V> klassV = (Class<V>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
		Map<K, List<V>> elements = new HashMap<>();
		JSONObject map = getObjectMap(this);
		if(map == null)
			Settings.resetMap(this);
		else
			for(String key : map.keySet())
			{
				K keyy = getKeyParser().apply(key);
				if(!elements.containsKey(key))
					elements.put(keyy, new ArrayList<>());
				JSONArray value = map.optJSONArray(key);
				if(value != null)
					value.toList().stream().map(klassV::cast).forEach(o -> elements.get(keyy).add(o));
			}
		return elements;
	}
	
	@Override
	public boolean isActionAllowed(SetConfigCommand.ChangeConfigType action)
	{
		return action == SetConfigCommand.ChangeConfigType.ADD || action == SetConfigCommand.ChangeConfigType.REMOVE || action == SetConfigCommand.ChangeConfigType.SHOW;
	}
}
