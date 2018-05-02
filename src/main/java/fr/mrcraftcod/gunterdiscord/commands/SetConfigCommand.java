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
		ADD, REMOVE, SET, SHOW, ERROR;
		
		public static ChangeConfigType get(String action)
		{
			for(ChangeConfigType type : ChangeConfigType.values())
				if(type.name().equalsIgnoreCase(action))
					return type;
			return ERROR;
		}
	}
	
	public enum ActionResult
	{
		OK, NONE, ERROR
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
		return super.getCommandDescription() + " <parametre> <action> [valeur...]";
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
				ActionResult result = processWithValue(event, configuration, args);
				if(result == ActionResult.OK)
					Actions.reply(event, "OK");
				else if(result == ActionResult.ERROR)
					Actions.reply(event, "Erreur - Fonctionnement de la commande: " + getCommandDescription() + " // " + configuration.getName());
			}
		}
		return CommandResult.SUCCESS;
	}
	
	private ActionResult processWithValue(MessageReceivedEvent event, Configuration configuration, LinkedList<String> args)
	{
		String actionStr;
		if((actionStr = args.poll()) == null)
			return ActionResult.ERROR;
		ChangeConfigType action = ChangeConfigType.get(actionStr);
		if(configuration.isActionAllowed(action))
		{
			try
			{
				return configuration.handleChange(event, action, args);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return ActionResult.ERROR;
	}
	
	@Override
	protected AccessLevel getAccessLevel()
	{
		return ADMIN;
	}
}
