package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.Settings;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
public class SetConfigCommand implements Command
{
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "Set config";
	}
	
	@Override
	public String getCommand()
	{
		return "set";
	}
	
	@Override
	public boolean execute(Settings settings, MessageReceivedEvent event, LinkedList<String> args)
	{
		if(args.size() > 0)
			switch(args.pop())
			{
				case "reportChan":
					if(args.size() > 0)
					{
						settings.setReportChannel(Long.parseLong(args.pop()));
						event.getChannel().sendMessage("Value set").complete();
					}
					break;
			}
		return false;
	}
}
