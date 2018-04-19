package fr.mrcraftcod.gunterdiscord.commands;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
public class QuizzCommand extends BasicCommand
{
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		if(super.execute(event, args) == CommandResult.NOT_ALLOWED)
			return CommandResult.NOT_ALLOWED;
		
		return CommandResult.SUCCESS;
	}
	
	@Override
	public String getCommandDescription()
	{
		return super.getCommandDescription() + " <action> <params>";
	}
	
	@Override
	protected AccessLevel getAccessLevel()
	{
		return AccessLevel.MODERATOR;
	}
	
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "Quizz";
	}
	
	@Override
	public String getCommand()
	{
		return "quizz";
	}
}
