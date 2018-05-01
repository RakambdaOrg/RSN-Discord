package fr.mrcraftcod.gunterdiscord.settings.configs;

import fr.mrcraftcod.gunterdiscord.commands.SetConfigCommand;
import fr.mrcraftcod.gunterdiscord.settings.MapListConfiguration;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class PhotoConfig extends MapListConfiguration<Long, String>
{
	@Override
	public SetConfigCommand.ActionResult handleChange(MessageReceivedEvent event, SetConfigCommand.ChangeConfigType action, LinkedList<String> args) throws Exception
	{
		if(action == SetConfigCommand.ChangeConfigType.SHOW)
		{
			Actions.reply(event, getAsMap().toString());
			return SetConfigCommand.ActionResult.NONE;
		}
		switch(action)
		{
			case ADD:
				if(args.size() < 2)
					return SetConfigCommand.ActionResult.ERROR;
				addValue(Long.parseLong(args.poll()), args.poll());
				return SetConfigCommand.ActionResult.OK;
			case REMOVE:
				if(args.size() < 1)
					return SetConfigCommand.ActionResult.ERROR;
				deleteKey(Long.parseLong(args.poll()));
				return SetConfigCommand.ActionResult.OK;
		}
		return SetConfigCommand.ActionResult.ERROR;
	}
	
	@Override
	protected Function<String, Long> getKeyParser()
	{
		return Long::parseLong;
	}
	
	@Override
	public String getName()
	{
		return "photo";
	}
	
	@Override
	protected BiFunction<Object, String, Boolean> getMatcher()
	{
		return (o1, o2) -> o1.toString().contains(o2);
	}
}
