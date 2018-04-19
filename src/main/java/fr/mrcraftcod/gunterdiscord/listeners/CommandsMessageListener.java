package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.commands.Command;
import fr.mrcraftcod.gunterdiscord.commands.ReportCommand;
import fr.mrcraftcod.gunterdiscord.commands.SetConfigCommand;
import fr.mrcraftcod.gunterdiscord.commands.StopCommand;
import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.PrefixConfig;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.io.InvalidClassException;
import java.util.ArrayList;
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
	private final List<Command> commands;
	
	public CommandsMessageListener()
	{
		commands = new ArrayList<>();
		commands.add(new StopCommand());
		commands.add(new SetConfigCommand());
		commands.add(new ReportCommand());
		//commands.add(new QuizCommand());
	}
	
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
						command.execute(event, args);
					}
					catch(Exception e)
					{
						event.getChannel().sendMessage("Cette fonctionnalité doit encore être configuré. Veuillez en avertir un modérateur.").complete();
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private boolean isCommand(String text)
	{
		try
		{
			return text.startsWith(new PrefixConfig().getString());
		}
		catch(InvalidClassException | NoValueDefinedException e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	private Command getCommand(String commandText)
	{
		for(Command command : commands)
			if(command.getCommand().equals(commandText))
				return command;
		
		return null;
	}
}
