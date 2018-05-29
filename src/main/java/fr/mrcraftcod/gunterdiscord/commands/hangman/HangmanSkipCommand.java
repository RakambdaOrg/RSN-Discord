package fr.mrcraftcod.gunterdiscord.commands.hangman;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.listeners.HangmanListener;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-27
 */
public class HangmanSkipCommand extends BasicCommand
{
	/**
	 * Constructor.
	 *
	 * @param parent The parent command.
	 */
	HangmanSkipCommand(Command parent)
	{
		super(parent);
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		super.execute(event, args);
		HangmanListener.getGame(event.getGuild(), false).ifPresent(h -> h.voteSkip(event.getMember()));
		return CommandResult.SUCCESS;
	}
	
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "Passer pendu";
	}
	
	@Override
	public List<String> getCommand()
	{
		return List.of("skip", "s");
	}
	
	@Override
	public String getDescription()
	{
		return "Passe son tour au pendu";
	}
	
	@Override
	public AccessLevel getAccessLevel()
	{
		return AccessLevel.ALL;
	}
}
