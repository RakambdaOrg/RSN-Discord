package fr.mrcraftcod.gunterdiscord.commands.generic;

import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.PrefixConfig;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.io.InvalidClassException;
import java.util.LinkedList;
import static fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand.AccessLevel.ALL;
import static fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand.AccessLevel.MODERATOR;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
public abstract class BasicCommand implements Command
{
	/**
	 * The level required to access a command.
	 */
	public enum AccessLevel
	{
		ADMIN, MODERATOR, ALL
	}
	
	/**
	 * Constructor.
	 */
	public BasicCommand()
	{
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		if(getAccessLevel() == ALL)
			return CommandResult.SUCCESS;
		if(getAccessLevel() == MODERATOR && Utilities.isModerator(event.getMember()))
			return CommandResult.SUCCESS;
		if(Utilities.isAdmin(event.getMember()))
			return CommandResult.SUCCESS;
		throw new NotAllowedException();
	}
	
	@Override
	public String getCommandUsage()
	{
		try
		{
			return new PrefixConfig().getString() + getCommand().get(0);
		}
		catch(InvalidClassException | NoValueDefinedException e)
		{
			e.printStackTrace();
		}
		return getCommand().get(0);
	}
	
	/**
	 * Get the level required to execute this command.
	 *
	 * @return The access level.
	 */
	protected abstract AccessLevel getAccessLevel();
}
