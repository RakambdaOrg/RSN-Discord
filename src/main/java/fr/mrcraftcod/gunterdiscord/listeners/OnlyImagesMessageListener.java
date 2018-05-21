package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.configs.OnlyImagesConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.io.InvalidClassException;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class OnlyImagesMessageListener extends ListenerAdapter
{
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		super.onMessageReceived(event);
		try
		{
			if(new OnlyImagesConfig().getAsList(event.getGuild()).contains(event.getMessage().getChannel().getIdLong()) && event.getMessage().getAttachments().size() < 1)
			{
				if(!Utilities.isTeam(event.getMember()))
				{
					Actions.deleteMessage(event.getMessage());
					Actions.replyPrivate(event.getAuthor(), "Le channel %s est pour les images seulement.", event.getChannel().getName());
				}
			}
		}
		catch(InvalidClassException e)
		{
			Log.error("Error getting images only list");
		}
	}
}
