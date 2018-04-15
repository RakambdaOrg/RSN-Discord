package fr.mrcraftcod.gunterdiscord.settings.configs;

import fr.mrcraftcod.gunterdiscord.commands.SetConfigCommand;
import fr.mrcraftcod.gunterdiscord.settings.ListConfiguration;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class ModoRolesConfig extends ListConfiguration<String>
{
	@SuppressWarnings("Duplicates")
	@Override
	public boolean handleChange(MessageReceivedEvent event, SetConfigCommand.ChangeConfigType action, LinkedList<String> args) throws Exception
	{
		if(action == SetConfigCommand.ChangeConfigType.SHOW)
		{
			Actions.reply(event, getAsList().stream().collect(Collectors.joining(", ")));
			return true;
		}
		if(args.size() < 1)
			return false;
		switch(action)
		{
			case ADD:
				addValue(args.poll());
				return true;
			case REMOVE:
				removeValue(args.poll());
				return true;
		}
		return false;
	}
	
	@Override
	public String getName()
	{
		return "modoRoles";
	}
}
