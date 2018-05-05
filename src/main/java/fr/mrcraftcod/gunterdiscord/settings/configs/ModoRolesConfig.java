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
	public SetConfigCommand.ActionResult handleChange(MessageReceivedEvent event, SetConfigCommand.ChangeConfigType action, LinkedList<String> args) throws Exception
	{
		if(action == SetConfigCommand.ChangeConfigType.SHOW)
		{
			Actions.reply(event, "Value: " + getAsList().stream().collect(Collectors.joining(", ")));
			return SetConfigCommand.ActionResult.NONE;
		}
		if(args.size() < 1)
			return SetConfigCommand.ActionResult.ERROR;
		switch(action)
		{
			case ADD:
				addValue(args.poll());
				return SetConfigCommand.ActionResult.OK;
			case REMOVE:
				removeValue(args.poll());
				return SetConfigCommand.ActionResult.OK;
		}
		return SetConfigCommand.ActionResult.ERROR;
	}
	
	@Override
	public String getName()
	{
		return "modoRoles";
	}
}
