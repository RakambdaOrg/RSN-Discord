package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.PrefixConfig;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.io.InvalidClassException;
import java.util.LinkedList;
import static fr.mrcraftcod.gunterdiscord.commands.BasicCommand.AccessLevel.ALL;
import static fr.mrcraftcod.gunterdiscord.commands.BasicCommand.AccessLevel.MODERATOR;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
public abstract class BasicCommand implements Command
{
	public enum AccessLevel
	{
		ADMIN, MODERATOR, ALL
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
		return CommandResult.NOT_ALLOWED;
	}
	
	@Override
	public String getCommandDescription()
	{
		try
		{
			return new PrefixConfig().getString() + getCommand();
		}
		catch(InvalidClassException | NoValueDefinedException e)
		{
			e.printStackTrace();
		}
		return getCommand();
	}
	
	protected abstract AccessLevel getAccessLevel();
}
