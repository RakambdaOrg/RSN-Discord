package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.settings.Configuration;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;
import static fr.mrcraftcod.gunterdiscord.commands.BasicCommand.AccessLevel.ADMIN;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
public class SetConfigCommand extends BasicCommand
{
	public enum ChangeConfigType
	{
		ADD, REMOVE, SET, ERROR;
		
		public static ChangeConfigType get(String action)
		{
			for(ChangeConfigType type : ChangeConfigType.values())
				if(type.name().equalsIgnoreCase(action))
					return type;
			return ERROR;
		}
	}
	
	@Override
	public int getScope()
	{
		return ChannelType.TEXT.getId();
	}
	
	@Override
	public String getName()
	{
		return "Set config";
	}
	
	@Override
	public String getCommand()
	{
		return "value";
	}
	
	@Override
	public String getCommandDescription()
	{
		return super.getCommandDescription() + " <parametre> <action> <valeur...>";
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		if(super.execute(event, args) == CommandResult.NOT_ALLOWED)
			return CommandResult.NOT_ALLOWED;
		if(args.size() > 0)
		{
			Configuration configuration = Settings.getSettings(args.pop());
			if(configuration != null)
			{
				if(processWithValue(configuration, args))
					Actions.reply(event, "Paramètre changé");
				else
					Actions.reply(event, "Fonctionnement de la commande: " + getCommand() + configuration);
			}
		}
		return CommandResult.SUCCESS;
	}
	
	private boolean processWithValue(Configuration configuration, LinkedList<String> args)
	{
		String actionStr;
		if((actionStr = args.poll()) == null)
			return false;
		ChangeConfigType action = ChangeConfigType.get(actionStr);
		if(configuration.isActionAllowed(action))
			return configuration.handleChange(action, args);
		return false;
	}
	
	@Override
	protected AccessLevel getAccessLevel()
	{
		return ADMIN;
	}
}
