package fr.mrcraftcod.gunterdiscord.commands;

import fr.mrcraftcod.gunterdiscord.commands.generic.BasicCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.CommandResult;
import fr.mrcraftcod.gunterdiscord.settings.Configuration;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import static fr.mrcraftcod.gunterdiscord.commands.generic.Command.AccessLevel.ADMIN;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 13/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-13
 */
public class SetConfigCommand extends BasicCommand //TODO Composite
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
	public List<String> getCommand()
	{
		return List.of("value", "v");
	}
	
	@Override
	public String getCommandUsage()
	{
		return super.getCommandUsage() + " <paramètre> <action> [valeur...]";
	}
	
	@Override
	public String getDescription()
	{
		return "Modifie la configuration du bot";
	}
	
	@Override
	public CommandResult execute(MessageReceivedEvent event, LinkedList<String> args) throws Exception
	{
		super.execute(event, args);
		if(args.size() > 0)
		{
			Configuration configuration = Settings.getSettings(args.pop());
			if(configuration != null)
			{
				ActionResult result = processWithValue(event, configuration, args);
				if(result == ActionResult.OK)
				{
					EmbedBuilder builder = new EmbedBuilder();
					builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
					builder.setColor(Color.GREEN);
					builder.setTitle("Valeur modifiée");
					Actions.reply(event, builder.build());
				}
				else if(result == ActionResult.ERROR)
				{
					EmbedBuilder builder = new EmbedBuilder();
					builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
					builder.setColor(Color.RED);
					builder.setTitle("Erreur lors de la modification");
					Actions.reply(event, builder.build());
				}
			}
			else
			{
				EmbedBuilder builder = new EmbedBuilder();
				builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
				builder.setColor(Color.ORANGE);
				builder.setTitle("Configuration non trouvée");
				Actions.reply(event, builder.build());
			}
		}
		else
		{
			EmbedBuilder builder = new EmbedBuilder();
			builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
			builder.setColor(Color.ORANGE);
			builder.setTitle("Merci de renseigner le nom de la configuration à changer");
			Actions.reply(event, builder.build());
		}
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
				EmbedBuilder builder = new EmbedBuilder();
				builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
				builder.setColor(Color.RED);
				builder.setTitle("Erreur durant l'opération");
				builder.setDescription("Commande: " + getName());
				builder.addField("Raison", e.getMessage(), false);
				Actions.reply(event, builder.build());
				Log.error("Error handling configuration change", e);
			}
		}
		return ActionResult.ERROR;
	}
	
	@Override
	public AccessLevel getAccessLevel()
	{
		return ADMIN;
	}
}
