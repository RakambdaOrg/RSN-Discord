package fr.mrcraftcod.gunterdiscord.settings;

import fr.mrcraftcod.gunterdiscord.commands.SetConfigCommand;
import java.util.LinkedList;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 15/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public abstract class Configuration
{
	public enum ConfigType
	{
		VALUE,
		LIST
	}
	
	public abstract boolean isActionAllowed(SetConfigCommand.ChangeConfigType action);
	
	public abstract boolean handleChange(SetConfigCommand.ChangeConfigType action, LinkedList<String> args);
	
	public abstract String getName();
	
	public abstract ConfigType getType();
}
