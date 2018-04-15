package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.NoValueDefinedException;
import fr.mrcraftcod.gunterdiscord.settings.configs.OnlyImagesConfig;
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
			if(event.getMessage().getAttachments().size() < 1 && new OnlyImagesConfig().getAsList().contains(event.getMessage().getChannel().getIdLong()))
			{
				if(!Utilities.isTeam(event.getMember()))
				{
					event.getMessage().delete().complete();
					event.getAuthor().openPrivateChannel().complete().sendMessageFormat("Le channel %s est pour les images seulement.", event.getChannel().getName()).complete();
				}
			}
		}
		catch(NoValueDefinedException | InvalidClassException e)
		{
			e.printStackTrace();
		}
	}
}
