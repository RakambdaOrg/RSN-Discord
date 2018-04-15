package fr.mrcraftcod.gunterdiscord.utils;

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
		event.getChannel().sendMessage(text).complete();
	}
}
