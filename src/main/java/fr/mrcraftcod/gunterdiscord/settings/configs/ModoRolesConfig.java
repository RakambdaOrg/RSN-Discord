package fr.mrcraftcod.gunterdiscord.settings.configs;

import fr.mrcraftcod.gunterdiscord.commands.SetConfigCommand;
import fr.mrcraftcod.gunterdiscord.settings.ListConfiguration;
import java.util.LinkedList;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class ModoRolesConfig extends ListConfiguration<String>
{
	@Override
	public boolean handleChange(SetConfigCommand.ChangeConfigType action, LinkedList<String> args)
	{
		return false;
	}
	
	@Override
	public String getName()
	{
		return "modoRoles";
	}
}
