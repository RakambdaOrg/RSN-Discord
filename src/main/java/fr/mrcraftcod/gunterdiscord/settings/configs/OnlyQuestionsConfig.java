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
public class OnlyQuestionsConfig extends ListConfiguration<Long>
{
	@SuppressWarnings("Duplicates")
	@Override
	public boolean handleChange(MessageReceivedEvent event, SetConfigCommand.ChangeConfigType action, LinkedList<String> args) throws Exception
	{
		if(action == SetConfigCommand.ChangeConfigType.SHOW)
		{
			Actions.reply(event, getAsList().stream().map(Object::toString).collect(Collectors.joining(", ")));
			return true;
		}
		if(args.size() < 1)
			return false;
		switch(action)
		{
			case ADD:
				addValue(Long.parseLong(args.poll()));
				return true;
			case REMOVE:
				removeValue(Long.parseLong(args.poll()));
				return true;
		}
		return false;
	}
	
	@Override
	public String getName()
	{
		return "onlyQuestion";
	}
}
