package fr.mrcraftcod.gunterdiscord;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 09/04/2018.
 *
 * @author Thomas Couchoud
 * @since 2018-04-09
 */
public class MessageListener extends ListenerAdapter
{
	private final Settings settings;
	
	public MessageListener(Settings settings)
	{
		this.settings = settings;
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		if(event.isFromType(ChannelType.PRIVATE))
		{
		}
		else
		{
			if(isBanned(event.getMessage().getContentRaw()))
			{
				event.getMessage().delete();
				event.getAuthor().openPrivateChannel().complete().sendMessageFormat("Restes poli s'il te plait :)").complete();
			}
		}
	}
	
	private boolean isBanned(String text)
	{
		text = text.toLowerCase();
		return text.contains("nazi") || text.contains("hitler");
	}
}
