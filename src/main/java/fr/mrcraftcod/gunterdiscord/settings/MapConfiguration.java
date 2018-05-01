package fr.mrcraftcod.gunterdiscord.settings;

import fr.mrcraftcod.gunterdiscord.commands.SetConfigCommand;
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
	
	public V getValue(K key)
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
			lastValue.put(key, value);
		Settings.mapValue(this, key, value);
	}
	
	public void deleteKey(K key)
	{
		if(lastValue != null)
			lastValue.remove(key);
		Settings.deleteKey(this, key);
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
	
	public Map<K, V> getAsMap() throws NoValueDefinedException, IllegalArgumentException, InvalidClassException
	{
		if(lastValue != null)
			return lastValue;
		Class<K> klassK = (Class<K>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		Class<V> klassV = (Class<V>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
		Map<K, V> elements = new HashMap<>();
		JSONObject map = getObjectMap(this);
		if(map == null)
			Settings.resetMap(this);
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
	
	@Override
	public boolean isActionAllowed(SetConfigCommand.ChangeConfigType action)
	{
		return action == SetConfigCommand.ChangeConfigType.ADD || action == SetConfigCommand.ChangeConfigType.REMOVE || action == SetConfigCommand.ChangeConfigType.SHOW;
	}
}
