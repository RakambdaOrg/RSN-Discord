package fr.mrcraftcod.gunterdiscord.settings;

import fr.mrcraftcod.gunterdiscord.commands.SetConfigCommand;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/05/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-05-12
 */
public abstract class MultipleRoleConfig extends ListConfiguration<Long>
{
	@Override
	public SetConfigCommand.ActionResult handleChange(MessageReceivedEvent event, SetConfigCommand.ChangeConfigType action, LinkedList<String> args) throws Exception
	{
		if(action == SetConfigCommand.ChangeConfigType.SHOW)
		{
			Actions.reply(event, "Value: " + getAsList().stream().map(Object::toString).collect(Collectors.joining(", ")));
			return SetConfigCommand.ActionResult.NONE;
		}
		if(args.size() < 1)
			return SetConfigCommand.ActionResult.ERROR;
		switch(action)
		{
			case ADD:
				addValue(event.getMessage().getMentionedRoles().get(0).getIdLong());
				return SetConfigCommand.ActionResult.OK;
			case REMOVE:
				removeValue(event.getMessage().getMentionedRoles().get(0).getIdLong());
				return SetConfigCommand.ActionResult.OK;
		}
		return SetConfigCommand.ActionResult.ERROR;
	}
}
