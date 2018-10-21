package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.configs.OnlyIdeasConfig;
import fr.mrcraftcod.gunterdiscord.utils.BasicEmotes;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class IdeaChannelMessageListener extends ListenerAdapter{
	@Override
	public void onMessageReceived(final MessageReceivedEvent event){
		super.onMessageReceived(event);
		try{
			if(new OnlyIdeasConfig(event.getGuild()).contains(event.getMessage().getTextChannel())){
				final var emotes = event.getGuild().getEmotes();
				emotes.stream().filter(e -> e.getName().equals("LLFthumbUp")).findFirst().ifPresentOrElse(e -> event.getMessage().addReaction(e).queue(), () -> event.getMessage().addReaction(BasicEmotes.THUMB_UP.getValue()).queue());
				emotes.stream().filter(e -> e.getName().equals("LLFthumbDown")).findFirst().ifPresentOrElse(e -> event.getMessage().addReaction(e).queue(), () -> event.getMessage().addReaction(BasicEmotes.THUMB_DOWN.getValue()).queue());
			}
		}
		catch(final Exception e){
			getLogger(event.getGuild()).error("", e);
		}
	}
}
