package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.commands.*;
import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCompositeCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.NotAllowedException;
import fr.mrcraftcod.gunterdiscord.commands.hangman.HangmanCompositeCommand;
import fr.mrcraftcod.gunterdiscord.commands.photo.PhotoCompositeCommand;
import fr.mrcraftcod.gunterdiscord.commands.quiz.QuizCompositeCommand;
import fr.mrcraftcod.gunterdiscord.commands.warevolves.WerewolvesCommandComposite;
import fr.mrcraftcod.gunterdiscord.settings.configs.PrefixConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.awt.*;
import java.io.InvalidClassException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
public class CommandsMessageListener extends ListenerAdapter
{
	public static final List<Command> commands = Arrays.asList(new PhotoCompositeCommand(), new HangmanCompositeCommand(), new QuizCompositeCommand(), new ReportCommand(), new ConfigurationCompositeCommand(), new StopCommand(), new HelpCommand(), new AvatarCommand(), new NicknameCommand(), new SayCommand(), new BackdoorCommand(), /*new QuestionCommand(),*/ new WerewolvesCommandComposite(), new VoiceDisrupterCommand());
	
	/**
	 * Constructor.
	 */
	public CommandsMessageListener()
	{
		HashMap<String, Integer> counts = new HashMap<>();
		commands.forEach(c -> c.getCommand().forEach(cmd -> counts.put(cmd, counts.getOrDefault(cmd, 0) + 1)));
		String clash = counts.keySet().stream().filter(k -> counts.get(k) > 1).collect(Collectors.joining(", "));
		if(clash != null && !clash.isEmpty())
			Log.error("Command clash: " + clash);
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		super.onMessageReceived(event);
		try
		{
			if(isCommand(event.getGuild(), event.getMessage().getContentRaw()))
			{
				Actions.deleteMessage(event.getMessage());
				LinkedList<String> args = new LinkedList<>(Arrays.asList(event.getMessage().getContentRaw().split(" ")));
				String cmdText = args.pop().substring(new PrefixConfig().getString(event.getGuild(), "g?").length());
				Command command = getCommand(cmdText);
				if(command != null)
				{
					if(command.getScope() == -5 || command.getScope() == event.getChannel().getType().getId())
					{
						try
						{
							Log.info("Executing command `" + cmdText + "`(" + command.getName() + ") from " + Actions.getUserToLog(event.getAuthor()) + ", args: " + args);
							switch(command.execute(event, args))
							{
								case NOT_ALLOWED:
									Actions.replyPrivate(event.getAuthor(), "Vous n'etes par autorisé à utiliser cette commande");
									break;
								case FAILED:
									Actions.replyPrivate(event.getAuthor(), "Une erreur est survenue");
									break;
								default:
								case SUCCESS:
							}
						}
						catch(NotAllowedException e)
						{
							Log.error("Error executing command " + command + " (not allowed)", e);
							EmbedBuilder builder = new EmbedBuilder();
							builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
							builder.setColor(Color.RED);
							builder.setTitle("Vous n'avez pas accès à cette commande.");
							Actions.reply(event, builder.build());
						}
						catch(Exception e)
						{
							Log.error("Error executing command " + command, e);
							EmbedBuilder builder = new EmbedBuilder();
							builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
							builder.setColor(Color.RED);
							builder.setTitle("Cette fonctionnalité doit encore être configuré. Veuillez en avertir un modérateur.");
							Actions.reply(event, builder.build());
						}
					}
					else
					{
						EmbedBuilder builder = new EmbedBuilder();
						builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
						builder.setColor(Color.ORANGE);
						builder.setTitle("Cette commande ne s'exécute pas dans ce type de channel");
						Actions.reply(event, builder.build());
					}
				}
				else
				{
					EmbedBuilder builder = new EmbedBuilder();
					builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
					builder.setColor(Color.ORANGE);
					builder.setTitle("Commande non trouvée");
					builder.addField("Commande", cmdText, false);
					Actions.reply(event, builder.build());
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
	 * @param guild The guild.
	 * @param text  The text.
	 *
	 * @return True if a command, false otherwise.
	 */
	private boolean isCommand(Guild guild, String text)
	{
		try
		{
			return text.startsWith(new PrefixConfig().getString(guild, "g?"));
		}
		catch(InvalidClassException e)
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
			if(command.getCommand().contains(commandText.toLowerCase()))
				return command;
		return null;
	}
}
