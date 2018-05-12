package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CallableCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
@CallableCommand
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
	public List<String> getCommand()
	{
		return List.of("stop");
	}
	
	@Override
	public String getDescription()
	{
		return "Arrête le bot";
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		super.execute(event, args);
		event.getJDA().shutdownNow();
		Log.info("BOT STOPPING");
		return CommandResult.SUCCESS;
	}
	
	@Override
	public AccessLevel getAccessLevel()
	{
		return AccessLevel.ADMIN;
	}
}
