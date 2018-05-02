package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.configs.BannedRegexConfig;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.io.InvalidClassException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class BannedRegexesMessageListener extends ListenerAdapter
{
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		super.onMessageReceived(event);
		String word = isBanned(event.getMessage().getContentRaw());
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
		}
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
		try
		{
			for(String regex : new BannedRegexConfig().getAsList())
			{
				Matcher matcher = Pattern.compile(regex).matcher(text);
				if(matcher.matches())
					return matcher.group(0);
			}
		}
		catch(InvalidClassException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
