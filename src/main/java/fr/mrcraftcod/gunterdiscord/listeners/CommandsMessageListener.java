package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.commands.*;
import fr.mrcraftcod.gunterdiscord.commands.config.ConfigurationCompositeCommand;
import fr.mrcraftcod.gunterdiscord.commands.generic.Command;
import fr.mrcraftcod.gunterdiscord.commands.generic.NotAllowedException;
import fr.mrcraftcod.gunterdiscord.commands.quiz.QuizCompositeCommand;
import fr.mrcraftcod.gunterdiscord.commands.warn.DoubleWarnCommand;
import fr.mrcraftcod.gunterdiscord.commands.warn.MegaWarnCommand;
import fr.mrcraftcod.gunterdiscord.commands.warn.NormalWarnCommand;
import fr.mrcraftcod.gunterdiscord.settings.configs.PrefixConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
public class CommandsMessageListener extends ListenerAdapter{
	public static final Command[] commands = new Command[]{
			// new PhotoCompositeCommand(),
			// new HangmanCompositeCommand(),
			new QuizCompositeCommand(),
			new ReportCommand(),
			new ConfigurationCompositeCommand(),
			new HelpCommand(),
			new AvatarCommand(),
			new NicknameCommand(),
			new SayCommand(),
			new BackdoorCommand(),
			new QuestionCommand(),
			// new WerewolvesCommandComposite(),
			new AnnoyCommand(),
			new UpCommand(),
			new YoutubeCommand(),
			// new MusicPartyCompositeCommand(),
			new NormalWarnCommand(),
			new DoubleWarnCommand(),
			new MegaWarnCommand(),
			new StopCommand(),
			new TimeCommand()
	};
	
	/**
	 * Constructor.
	 */
	public CommandsMessageListener(){
		HashMap<String, Integer> counts = new HashMap<>();
		Arrays.asList(commands).forEach(c -> c.getCommand().forEach(cmd -> counts.put(cmd, counts.getOrDefault(cmd, 0) + 1)));
		String clash = counts.keySet().stream().filter(k -> counts.get(k) > 1).collect(Collectors.joining(", "));
		if(clash != null && !clash.isEmpty()){
			getLogger(null).error("Command clash: {}", clash);
		}
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event){
		super.onMessageReceived(event);
		try{
			if(isCommand(event.getGuild(), event.getMessage().getContentRaw())){
				Actions.deleteMessage(event.getMessage());
				LinkedList<String> args = new LinkedList<>(Arrays.asList(event.getMessage().getContentRaw().split(" ")));
				String cmdText = args.pop().substring(new PrefixConfig(event.getGuild()).getObject("g?").length());
				Command command = getCommand(cmdText);
				if(command != null){
					if(command.getScope() == -5 || command.getScope() == event.getChannel().getType().getId()){
						try{
							getLogger(event.getGuild()).info("Executing command `{}`({}) from {}, args: {}", cmdText, command.getName(), Utilities.getUserToLog(event.getAuthor()), args);
							switch(command.execute(event, args)){
								case NOT_ALLOWED:
									Actions.replyPrivate(event.getGuild(), event.getAuthor(), "Vous n'etes par autorisé à utiliser cette commande");
									break;
								case FAILED:
									Actions.replyPrivate(event.getGuild(), event.getAuthor(), "Une erreur est survenue");
									break;
								default:
								case SUCCESS:
							}
						}
						catch(NotAllowedException e){
							getLogger(event.getGuild()).error("Error executing command {} (not allowed)", command, e);
							EmbedBuilder builder = new EmbedBuilder();
							builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
							builder.setColor(Color.RED);
							builder.setTitle("Vous n'avez pas accès à cette commande.");
							Actions.reply(event, builder.build());
						}
						catch(Exception e){
							getLogger(event.getGuild()).error("Error executing command {}", command, e);
							EmbedBuilder builder = new EmbedBuilder();
							builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
							builder.setColor(Color.RED);
							builder.setTitle("Cette fonctionnalité doit encore être configuré. Veuillez en avertir un modérateur.");
							Actions.reply(event, builder.build());
						}
					}
					else{
						EmbedBuilder builder = new EmbedBuilder();
						builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
						builder.setColor(Color.ORANGE);
						builder.setTitle("Cette commande ne s'exécute pas dans ce type de channel");
						Actions.reply(event, builder.build());
					}
				}
				else{
					EmbedBuilder builder = new EmbedBuilder();
					builder.setAuthor(event.getAuthor().getName(), null, event.getAuthor().getAvatarUrl());
					builder.setColor(Color.ORANGE);
					builder.setTitle("Commande non trouvée");
					builder.addField("Commande", cmdText, false);
					Actions.reply(event, builder.build());
				}
			}
		}
		catch(Exception e){
			getLogger(event.getGuild()).error("Error handling message", e);
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
	private static boolean isCommand(Guild guild, String text){
		return text.startsWith(new PrefixConfig(guild).getObject("g?"));
	}
	
	/**
	 * get the command associated to this string.
	 *
	 * @param commandText The command text.
	 *
	 * @return The command or null if not found.
	 */
	private static Command getCommand(String commandText){
		for(Command command : commands){
			if(command.getCommand().contains(commandText.toLowerCase())){
				return command;
			}
		}
		return null;
	}
}
