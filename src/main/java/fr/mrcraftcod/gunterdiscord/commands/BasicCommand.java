package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
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
	public boolean execute(Settings settings, MessageReceivedEvent event, LinkedList<String> args)
	{
		if(getAccessLevel() == ALL)
			return true;
		if(getAccessLevel() == MODERATOR && Main.utilities.isModerator(event.getMember()))
			return true;
		return Main.utilities.isAdmin(event.getMember());
	}
	
	protected abstract AccessLevel getAccessLevel();
}
