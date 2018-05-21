package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.configs.OnlyIdeasConfig;
import fr.mrcraftcod.gunterdiscord.utils.BasicEmotes;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.io.InvalidClassException;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class IdeaChannelMessageListener extends ListenerAdapter
{
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		super.onMessageReceived(event);
		try
		{
			if(new OnlyIdeasConfig().getAsList(event.getGuild()).contains(event.getMessage().getChannel().getIdLong()))
			{
				event.getMessage().addReaction(BasicEmotes.THUMB_UP.getValue()).complete();
				event.getMessage().addReaction(BasicEmotes.THUMB_DOWN.getValue()).complete();
			}
		}
		catch(InvalidClassException e)
		{
			Log.error("Error getting idea channel list");
		}
	}
}
