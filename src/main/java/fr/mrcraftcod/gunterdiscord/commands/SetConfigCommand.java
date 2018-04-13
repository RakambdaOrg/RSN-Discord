package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.Settings;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;
import static fr.mrcraftcod.gunterdiscord.commands.BasicCommand.AccessLevel.ADMIN;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
public class SetConfigCommand extends BasicCommand
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
		return "value";
	}
	
	@Override
	public boolean execute(Settings settings, MessageReceivedEvent event, LinkedList<String> args)
	{
		if(!super.execute(settings, event, args))
			return false;
		if(args.size() > 0)
			switch(args.pop())
			{
				case "set":
					if(args.size() > 0)
						setValue(settings, event, args);
					break;
				case "add":
					if(args.size() > 0)
						addValue(settings, event, args);
					break;
				case "remove":
					if(args.size() > 0)
						removeValue(settings, event, args);
					break;
			}
		return false;
	}
	
	private void setValue(Settings settings, MessageReceivedEvent event, LinkedList<String> args)
	{
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
	}
	
	private void addValue(Settings settings, MessageReceivedEvent event, LinkedList<String> args)
	{
		switch(args.pop())
		{
			case "modoRole":
				if(args.size() > 0)
				{
					settings.addModoRole(args.pop());
					event.getChannel().sendMessage("Value added").complete();
				}
				break;
		}
	}
	
	private void removeValue(Settings settings, MessageReceivedEvent event, LinkedList<String> args)
	{
		switch(args.pop())
		{
			case "modoRole":
				if(args.size() > 0)
				{
					settings.removeModoRole(args.pop());
					event.getChannel().sendMessage("Value removed").complete();
				}
				break;
		}
	}
	
	@Override
	protected AccessLevel getAccessLevel()
	{
		return ADMIN;
	}
}
