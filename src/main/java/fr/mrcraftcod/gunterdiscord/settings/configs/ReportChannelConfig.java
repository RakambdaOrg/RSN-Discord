package fr.mrcraftcod.gunterdiscord.settings.configs;

import fr.mrcraftcod.gunterdiscord.commands.SetConfigCommand;
import fr.mrcraftcod.gunterdiscord.settings.ValueConfiguration;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class ReportChannelConfig extends ValueConfiguration
{
	@Override
	public boolean handleChange(MessageReceivedEvent event, SetConfigCommand.ChangeConfigType action, LinkedList<String> args) throws Exception
	{
		if(action == SetConfigCommand.ChangeConfigType.SHOW)
		{
			Actions.reply(event, getObject().toString());
			return true;
		}
		if(args.size() < 1)
			return false;
		switch(action)
		{
			case SET:
				setValue(Long.parseLong(args.poll()));
				return true;
		}
		return false;
	}
	
	@Override
	public String getName()
	{
		return "reportChannel";
	}
}
