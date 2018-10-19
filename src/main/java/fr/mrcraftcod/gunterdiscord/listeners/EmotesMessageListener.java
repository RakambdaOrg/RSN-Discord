package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.configs.EmoteUsageConfig;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.util.Optional;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class EmotesMessageListener extends ListenerAdapter{
	@Override
	public void onMessageReceived(final MessageReceivedEvent event){
		super.onMessageReceived(event);
		try{
			final var config = new EmoteUsageConfig(event.getGuild());
			event.getMessage().getEmotes().stream().map(e -> e.getName() + ":" + e.getId()).forEach(id -> {
				config.addValue(id, Optional.ofNullable(config.getValue(id)).orElse(0L) + 1);
			});
		}
		catch(final Exception e){
			getLogger(event.getGuild()).error("", e);
		}
	}
}
