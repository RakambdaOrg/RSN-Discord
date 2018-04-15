package fr.mrcraftcod.gunterdiscord.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
public interface Command
{
	int getScope();
	String getName();
	String getCommand();
	String getCommandDescription();
	CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception;
}
