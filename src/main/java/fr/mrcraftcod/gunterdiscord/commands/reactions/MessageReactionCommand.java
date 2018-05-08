package fr.mrcraftcod.gunterdiscord.commands.reactions;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-07
 */
public class MessageReactionCommand extends BasicCommand
{
	@Override
	protected AccessLevel getAccessLevel()
	{
		return AccessLevel.ADMIN;
	}
	
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "Message reaction";
	}
	
	@Override
	public List<String> getCommand()
	{
		return List.of("reactionmessage", "rm");
	}
	
	@Override
	public String getDescription()
	{
		return "Définit un message comme supportant des réactions spécifiques";
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		super.execute(event, args);
		
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandUsage()
	{
		return super.getCommandUsage() + " <ID> <unique|multi>";
	}
}
