package fr.mrcraftcod.gunterdiscord.listeners;

import fr.mrcraftcod.gunterdiscord.settings.NewSettings;
import fr.mrcraftcod.gunterdiscord.utils.BasicEmotes;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import javax.annotation.Nonnull;
import java.util.Objects;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-04-15
 */
public class IdeaChannelMessageListener extends ListenerAdapter{
	@Override
	public void onGuildMessageReceived(@Nonnull final GuildMessageReceivedEvent event){
		super.onGuildMessageReceived(event);
		try{
			if(NewSettings.getConfiguration(event.getGuild()).getIdeaChannels().stream().anyMatch(c -> Objects.equals(c.getChannelId(), event.getChannel().getIdLong()))){
				event.getMessage().addReaction(BasicEmotes.THUMB_UP.getValue()).complete();
				event.getMessage().addReaction(BasicEmotes.THUMB_DOWN.getValue()).complete();
			}
		}
		catch(final Exception e){
			getLogger(event.getGuild()).error("", e);
		}
	}
}
