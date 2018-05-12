package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CallableCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.listeners.HangmanListener;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Roles;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
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
public class HangmanCommand extends BasicCommand
{
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		super.execute(event, args);
		if(HangmanListener.resetting)
		{
			Actions.replyPrivate(event.getAuthor(), "Patiente quelques secondes, le pendu se réinitialise");
			return CommandResult.SUCCESS;
		}
		if(!Utilities.hasRole(event.getMember(), Roles.HANGMAN))
		{
			Actions.giveRole(event.getGuild(), event.getAuthor(), Roles.HANGMAN);
			HangmanListener.setUp(event.getGuild());
			HangmanListener.onPlayerJoin(event.getMember());
		}
		return CommandResult.SUCCESS;
	}
	
	@Override
	public AccessLevel getAccessLevel()
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
	public List<String> getCommand()
	{
		return List.of("pendu");
	}
	
	@Override
	public String getDescription()
	{
		return "Lance une partie de pendu";
	}
}
