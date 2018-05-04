package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CallableCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.Configuration;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.util.LinkedList;
import static fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand.AccessLevel.ADMIN;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
@CallableCommand
public class SetConfigCommand extends BasicCommand
{
	/**
	 * Actions available.
	 */
	public enum ChangeConfigType
	{
		ADD, REMOVE, SET, SHOW, ERROR;
		
		/**
		 * Get an action by its name.
		 *
		 * @param action The action to find.
		 *
		 * @return The action or ERROR if not found.
		 */
		public static ChangeConfigType get(String action)
		{
			for(ChangeConfigType type : ChangeConfigType.values())
				if(type.name().equalsIgnoreCase(action))
					return type;
			return ERROR;
		}
	}
	
	/**
	 * The result of a setting action.
	 */
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
		return super.getCommandDescription() + " <paramètre> <action> [valeur...]";
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
					Actions.reply(event, "Erreur - Fonctionnement de la commande: %s // %s", getCommandDescription(), configuration.getName());
			}
			else
				Actions.reply(event, "Configuration non trouvée");
		}
		else
			Actions.reply(event, "Merci de renseigner un paramètre: %s", getCommandDescription());
		return CommandResult.SUCCESS;
	}
	
	/**
	 * Process the action on a given configuration.
	 *
	 * @param event         The event that triggered this event.
	 * @param configuration The configuration concerned.
	 * @param args          The arguments passed.
	 *
	 * @return The result of this action on this config.
	 */
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
				Log.info("Handling change " + action + " on config " + configuration + " with parameters " + args);
				return configuration.handleChange(event, action, args);
			}
			catch(Exception e)
			{
				Log.error("Error handling configuration change", e);
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
