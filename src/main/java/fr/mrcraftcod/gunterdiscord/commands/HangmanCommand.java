package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CallableCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.listeners.HangmanListener;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Roles;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
@CallableCommand
public class HangmanCommand extends BasicCommand
{
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		Actions.deleteMessage(event.getMessage());
		if(super.execute(event, args) == CommandResult.NOT_ALLOWED)
			return CommandResult.NOT_ALLOWED;
		if(HangmanListener.resetting)
			return CommandResult.SUCCESS;
		Role role = Utilities.getRole(event.getJDA(), Roles.HANGMAN);
		if(role != null)
		{
			if(event.getMember().getRoles().contains(role))
				return CommandResult.SUCCESS;
			Actions.giveRole(event.getGuild(), event.getAuthor(), role);
			HangmanListener.setUp(event.getGuild());
			HangmanListener.onPlayerJoin(event.getMember());
		}
		else
			return CommandResult.FAILED;
		return CommandResult.SUCCESS;
	}
	
	@Override
	protected AccessLevel getAccessLevel()
	{
		return AccessLevel.ALL;
	}
	
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "Pendu";
	}
	
	@Override
	public String getCommand()
	{
		return "pendu";
	}
}
