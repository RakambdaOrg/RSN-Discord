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
	public void addValue(T value)
	{
		Settings.addValue(this, value);
	}
	
	public void removeValue(T value)
	{
		Settings.removeValue(this, value);
	}
	
	@Override
	public ConfigType getType()
	{
		return ConfigType.LIST;
	}
	
	private static JSONArray getObjectList(Configuration configuration) throws IllegalArgumentException
	{
		if(configuration.getType() != ConfigType.LIST)
			throw new IllegalArgumentException("Not a list config");
		return Settings.getArray(configuration.getName());
	}
	
	public List<T> getAsList() throws NoValueDefinedException, IllegalArgumentException, InvalidClassException
	{
		Class<T> klass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		List<T> elements = new LinkedList<>();
		JSONArray array = getObjectList(this);
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
		return action == SetConfigCommand.ChangeConfigType.ADD || action == SetConfigCommand.ChangeConfigType.REMOVE;
	}
}
