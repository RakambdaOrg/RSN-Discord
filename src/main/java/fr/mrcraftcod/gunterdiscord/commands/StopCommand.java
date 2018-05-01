package fr.mrcraftcod.gunterdiscord.commands;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public class StopCommand extends BasicCommand
{
	
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "Stop";
	}
	
	@Override
	public String getCommand()
	{
		return "stop";
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		if(super.execute(event, args) == CommandResult.NOT_ALLOWED)
			return CommandResult.NOT_ALLOWED;
		event.getJDA().shutdownNow();
		return CommandResult.SUCCESS;
	}
	
	@Override
	protected AccessLevel getAccessLevel()
	{
		return AccessLevel.ADMIN;
	}
}
