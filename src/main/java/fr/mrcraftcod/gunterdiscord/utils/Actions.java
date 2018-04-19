package fr.mrcraftcod.gunterdiscord.utils;

import fr.mrcraftcod.gunterdiscord.Main;
import net.dv8tion.jda.core.entities.Message;
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
	public static Message reply(MessageReceivedEvent event, String text)
	{
		return sendMessage(event.getTextChannel(), text);
	}
	
	public static Message sendMessage(TextChannel channel, String text)
	{
		if(channel.canTalk())
			return channel.sendMessage(text).complete();
		else
			reportError("Access denied to text channel: " + channel.getAsMention());
		return null;
	}
	
	private static void reportError(String text)
	{
		//TODO
	}
	
	public static Message sendMessage(long channelID, String text)
	{
		return sendMessage(Main.getJDA().getTextChannelById(channelID), text);
	}
}
