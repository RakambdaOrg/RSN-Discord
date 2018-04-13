package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.Settings;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public class PrefixCommand implements Command
{
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "Prefix";
	}
	
	@Override
	public String getCommand()
	{
		return "prefix";
	}
	
	@Override
	public boolean execute(Settings settings, MessageReceivedEvent event, LinkedList<String> args)
	{
		if(args.size() > 0)
			return settings.setPrefix(args.pop());
		return false;
	}
}
