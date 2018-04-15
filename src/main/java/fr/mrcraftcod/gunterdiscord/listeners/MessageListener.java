package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.Main;
import fr.mrcraftcod.gunterdiscord.settings.Settings;
import fr.mrcraftcod.gunterdiscord.commands.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
public class MessageListener extends ListenerAdapter
{
	private final Settings settings;
	private final List<Command> commands;
	
	public MessageListener(Settings settings)
	{
		this.settings = settings;
		commands = new ArrayList<>();
		commands.add(new PrefixCommand());
		commands.add(new StopCommand());
		commands.add(new SetConfigCommand());
		commands.add(new ReportCommand());
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		if(isCommand(event.getMessage().getContentRaw()))
		{
			LinkedList<String> args = new LinkedList<>();
			args.addAll(Arrays.asList(event.getMessage().getContentRaw().split(" ")));
			Command command = getCommand(args.pop().substring(settings.getPrefix().length()));
			if(command != null && (command.getScope() == -5 || command.getScope() == event.getChannel().getType().getId()))
				command.execute(settings, event, args);
		}
		
		if(true)
		{
			/*String word = isBanned(event.getMessage().getContentRaw());
			if(word != null)
			{
				event.getMessage().delete().complete();
				event.getMessage().getChannel().sendMessage("Attention " + event.getAuthor().getAsMention() + ", l'utilisation de mot prohibés va finir par te bruler le derrière.").complete();
				try
				{
					event.getAuthor().openPrivateChannel().complete(false).sendMessageFormat("Restes poli s'il te plait :). Le mot " + getCensoredWord(word) + " est prohibé.").complete();
				}
				catch(RateLimitedException ignored)
				{
				}
			}*/
			
			if(event.getMessage().getAttachments().size() < 1 && settings.isImageOnly(event.getMessage().getChannel().getIdLong()))
			{
				if(!Main.utilities.isTeam(event.getMember()))
				{
					event.getMessage().delete().complete();
					event.getAuthor().openPrivateChannel().complete().sendMessageFormat("Le channel " + event.getChannel().getName() + " est pour les images seulement.").complete();
				}
			}
			
			if(!event.getMessage().getContentRaw().trim().endsWith("?") && settings.isQuestionOnly(event.getMessage().getChannel().getIdLong()))
			{
				if(!Main.utilities.isTeam(event.getMember()))
				{
					event.getMessage().delete().complete();
					event.getAuthor().openPrivateChannel().complete().sendMessageFormat("Le channel " + event.getChannel().getName() + " est pour les questions seulement (ça finit par un '?'). Ton message était: " + event.getMessage().getContentRaw()).complete();
				}
			}
		}
	}
	
	private Command getCommand(String commandText)
	{
		for(Command command : commands)
			if(command.getCommand().equals(commandText))
				return command;
		
		return null;
	}
	
	private boolean isCommand(String text)
	{
		return text.startsWith(settings.getPrefix());
	}
	
	private String getCensoredWord(String word)
	{
		if(word.length() > 2)
		{
			StringBuilder sb = new StringBuilder(word);
			sb.replace(1, word.length() - 1, "********************************************");
			return sb.toString();
		}
		return word;
	}
	
	private String isBanned(String text)
	{
		text = text.toLowerCase();
		for(String regex : settings.getBannedWords())
		{
			Matcher matcher = Pattern.compile(regex).matcher(text);
			if(matcher.matches())
				return matcher.group(0);
		}
		return null;
	}
}
