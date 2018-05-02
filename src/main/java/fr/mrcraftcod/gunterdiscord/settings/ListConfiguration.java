package fr.mrcraftcod.gunterdiscord.settings;

import fr.mrcraftcod.gunterdiscord.commands.SetConfigCommand;
import org.json.JSONArray;
import java.io.InvalidClassException;
import java.lang.reflect.ParameterizedType;
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
	 * @param value The value to add.
	 */
	public void addValue(T value)
	{
		if(lastValue != null)
			lastValue.add(value);
		Settings.addValue(this, value);
	}
	
	/**
	 * Remove a value from the list.
	 *
	 * @param value The value to remove.
	 */
	public void removeValue(T value)
	{
		if(lastValue != null)
			lastValue.remove(value);
		Settings.removeValue(this, value);
	}
	
	@Override
	public ConfigType getType()
	{
		return ConfigType.LIST;
	}
	
	/**
	 * Get the JSON array.
	 *
	 * @return The JSON array.
	 *
	 * @throws IllegalArgumentException If the configuration isn't a list.
	 */
	private JSONArray getObjectList() throws IllegalArgumentException
	{
		if(getType() != ConfigType.LIST)
			throw new IllegalArgumentException("Not a list config");
		return Settings.getArray(getName());
	}
	
	/**
	 * Get a list of the values.
	 *
	 * @return The values list.
	 *
	 * @throws IllegalArgumentException If the configuration isn't a list.
	 * @throws InvalidClassException    If the values are not of type T.
	 */
	public List<T> getAsList() throws IllegalArgumentException, InvalidClassException
	{
		if(lastValue != null)
			return lastValue;
		@SuppressWarnings("unchecked")
		Class<T> klass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		List<T> elements = new LinkedList<>();
		JSONArray array = getObjectList();
		if(array == null)
			Settings.resetList(this);
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
	
	@Override
	public boolean isActionAllowed(SetConfigCommand.ChangeConfigType action)
	{
		return action == SetConfigCommand.ChangeConfigType.ADD || action == SetConfigCommand.ChangeConfigType.REMOVE || action == SetConfigCommand.ChangeConfigType.SHOW;
	}
}
