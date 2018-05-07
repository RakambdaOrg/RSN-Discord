package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.commands.*;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.photo.AddPhotoCommand;
import fr.mrcraftcod.gunterdiscord.commands.photo.DelPhotoCommand;
import fr.mrcraftcod.gunterdiscord.commands.photo.PhotoCommand;
import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.PrefixConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.io.InvalidClassException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
public class CommandsMessageListener extends ListenerAdapter
{
	private static final List<Command> commands = Arrays.asList(new AddPhotoCommand(), new DelPhotoCommand(), new PhotoCommand(), new HangmanCommand(), new QuizCommand(), new ReportCommand(), new SetConfigCommand(), new StopCommand(), new CandidCommand());
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		super.onMessageReceived(event);
		try
		{
			if(isCommand(event.getMessage().getContentRaw()))
			{
				LinkedList<String> args = new LinkedList<>();
				args.addAll(Arrays.asList(event.getMessage().getContentRaw().split(" ")));
				Command command = getCommand(args.pop().substring(new PrefixConfig().getString("g?").length()));
				if(command != null && (command.getScope() == -5 || command.getScope() == event.getChannel().getType().getId()))
				{
					try
					{
						Log.info("Executing command `" + command.getName() + " from " + Actions.getUserToLog(event.getAuthor()) + ", args: " + args);
						command.execute(event, args);
					}
					catch(Exception e)
					{
						Log.error("Error executing command " + command, e);
						Actions.reply(event, "Cette fonctionnalité doit encore être configuré. Veuillez en avertir un modérateur.");
					}
				}
			}
		}
		catch(Exception e)
		{
			Log.error("", e);
		}
	}
	
	/**
	 * Tell if this text is a command.
	 *
	 * @param text The text.
	 *
	 * @return True if a command, false otherwise.
	 */
	private boolean isCommand(String text)
	{
		try
		{
			return text.startsWith(new PrefixConfig().getString());
		}
		catch(InvalidClassException | NoValueDefinedException e)
		{
			Log.warning("Error testing command", e);
		}
		return false;
	}
	
	/**
	 * get the command associated to this string.
	 *
	 * @param commandText The command text.
	 *
	 * @return The command or null if not found.
	 */
	private Command getCommand(String commandText)
	{
		for(Command command : commands)
			if(command.getCommand().equalsIgnoreCase(commandText))
				return command;
		return null;
	}
}
