package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.configs.OnlyQuestionsConfig;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class OnlyQuestionsMessageListener extends ListenerAdapter
{
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		super.onMessageReceived(event);
		try
		{
			if(!event.getMessage().getContentRaw().trim().endsWith("?") && new OnlyQuestionsConfig().getAsList().contains(event.getMessage().getChannel().getIdLong()))
			{
				if(!Utilities.isTeam(event.getMember()))
				{
					event.getMessage().delete().complete();
					event.getAuthor().openPrivateChannel().complete().sendMessageFormat("Le channel " + event.getChannel().getName() + " est pour les questions seulement (ça finit par un '?'). Ton message était: " + event.getMessage().getContentRaw()).complete();
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
