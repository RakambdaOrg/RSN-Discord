package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.configs.OnlyImagesConfig;
import fr.mrcraftcod.gunterdiscord.utils.Actions;
import fr.mrcraftcod.gunterdiscord.utils.Utilities;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class OnlyImagesMessageListener extends ListenerAdapter{
	@Override
	public void onMessageReceived(final MessageReceivedEvent event){
		super.onMessageReceived(event);
		try{
			if(new OnlyImagesConfig(event.getGuild()).contains(event.getMessage().getTextChannel()) && event.getMessage().getAttachments().size() < 1){
				if(!Utilities.isTeam(event.getMember())){
					Actions.deleteMessage(event.getMessage());
					Actions.replyPrivate(event.getGuild(), event.getAuthor(), "The channel %s is only for images.", event.getChannel().getName());
				}
			}
		}
		catch(final Exception e){
			getLogger(event.getGuild()).error("", e);
		}
	}
}
