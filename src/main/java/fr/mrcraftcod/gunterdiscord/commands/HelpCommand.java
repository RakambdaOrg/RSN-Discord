package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CallableCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.listeners.CommandsMessageListener;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 12/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-12
 */
@CallableCommand
public class HelpCommand extends BasicCommand
{
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "Help";
	}
	
	@Override
	public List<String> getCommand()
	{
		return List.of("help", "h");
	}
	
	@Override
	public String getCommandUsage()
	{
		return super.getCommandUsage() + " [commande]";
	}
	
	@Override
	public String getDescription()
	{
		return "Aide des commandes";
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		super.execute(event, args);
		if(args.size() < 1)
			Actions.reply(event, "Liste des commandes disponibles:\n%s", CommandsMessageListener.commands.stream().map(s -> s.getCommand().get(0) + " : " + s.getDescription()).sorted().collect(Collectors.joining("\n")));
		else
			Actions.reply(event, CommandsMessageListener.commands.stream().filter(s -> s.getCommand().contains(args.get(0).toLowerCase())).map(Command::getCommandUsage).findAny().orElse(null));
		return CommandResult.SUCCESS;
	}
	
	@Override
	protected AccessLevel getAccessLevel()
	{
		return AccessLevel.ADMIN;
	}
}
