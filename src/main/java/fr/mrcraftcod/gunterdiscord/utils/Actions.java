package fr.mrcraftcod.gunterdiscord.utils;

import fr.mrcraftcod.gunterdiscord.Main;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class Actions
{
	public static void reply(MessageReceivedEvent event, String text)
	{
		sendMessage(event.getTextChannel(), text);
	}
	
	public static void sendMessage(TextChannel channel, String text)
	{
		if(channel.canTalk())
			channel.sendMessage(text).complete();
		else
			reportError("Access denied to text channel: " + channel.getAsMention());
	}
	
	private static void reportError(String text)
	{
		//TODO
	}
	
	public static void sendMessage(long channelID, String text)
	{
		sendMessage(Main.getJDA().getTextChannelById(channelID), text);
	}
}
