package fr.raksrinana.rsndiscord.listeners;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.Objects;

public class AutoReactionsChannelMessageListener extends ListenerAdapter{
	@Override
	public void onGuildMessageReceived(@NonNull final GuildMessageReceivedEvent event){
		super.onGuildMessageReceived(event);
		try{
			if(Settings.get(event.getGuild()).getAutoThumbsChannels().stream().anyMatch(c -> Objects.equals(c.getChannelId(), event.getChannel().getIdLong()))){
				Actions.addReaction(event.getMessage(), BasicEmotes.THUMB_UP.getValue());
				Actions.addReaction(event.getMessage(), BasicEmotes.THUMB_DOWN.getValue());
			}
			if(Settings.get(event.getGuild()).getAutoReactionsChannels().stream().anyMatch(c -> Objects.equals(c.getChannelId(), event.getChannel().getIdLong()))){
				event.getMessage().getEmotes().forEach(emote -> Actions.addReaction(event.getMessage(), emote));
			}
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("", e);
		}
	}
}
