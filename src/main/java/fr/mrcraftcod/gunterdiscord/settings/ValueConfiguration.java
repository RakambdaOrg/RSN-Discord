package fr.mrcraftcod.gunterdiscord.settings;

import fr.mrcraftcod.gunterdiscord.commands.SetConfigCommand;
import java.io.InvalidClassException;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public abstract class ValueConfiguration extends Configuration
{
	@Override
	public ConfigType getType()
	{
		return ConfigType.VALUE;
	}
	
	protected Object getObject() throws IllegalArgumentException
	{
		if(getType() != ConfigType.VALUE)
			throw new IllegalArgumentException("Not a value config");
		return Settings.getObject(getName());
	}
	
	public String getString() throws InvalidClassException, IllegalArgumentException, NoValueDefinedException
	{
		Object value = getObject();
		if(value == null)
			throw new NoValueDefinedException(this);
		if(value instanceof String)
			return (String) value;
		throw new InvalidClassException("Config is not a string");
	}
	
	public String getString(String defaultValue) throws InvalidClassException, IllegalArgumentException
	{
		Object value = getObject();
		if(value == null)
			return defaultValue;
		if(value instanceof String)
			return (String) value;
		throw new InvalidClassException("Config is not a string");
	}
	
	public long getLong() throws InvalidClassException, IllegalArgumentException, NoValueDefinedException
	{
		Object value = getObject();
		if(value == null)
			throw new NoValueDefinedException(this);
		if(value instanceof Long)
			return (Long) value;
		throw new InvalidClassException("Config is not a long");
	}
	
	public void setValue(Object value)
	{
		Settings.setValue(this, value);
	}
	
	@Override
	public boolean isActionAllowed(SetConfigCommand.ChangeConfigType action)
	{
		return action == SetConfigCommand.ChangeConfigType.SET;
	}
}
