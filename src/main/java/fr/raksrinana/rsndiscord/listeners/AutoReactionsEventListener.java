package fr.raksrinana.rsndiscord.listeners;

import com.vdurmont.emoji.EmojiParser;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.BasicEmotes;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.Objects;

@EventListener
public class AutoReactionsEventListener extends ListenerAdapter{
	@Override
	public void onGuildMessageReceived(@NonNull final GuildMessageReceivedEvent event){
		super.onGuildMessageReceived(event);
		try{
			if(Settings.get(event.getGuild()).getAutoThumbsChannels().stream().anyMatch(c -> Objects.equals(c.getChannelId(), event.getChannel().getIdLong()))){
				Actions.addReaction(event.getMessage(), BasicEmotes.THUMB_UP.getValue());
				Actions.addReaction(event.getMessage(), BasicEmotes.THUMB_DOWN.getValue());
			}
			if(Settings.get(event.getGuild()).getAutoReactionsChannels().stream().anyMatch(c -> Objects.equals(c.getChannelId(), event.getChannel().getIdLong()))){
				event.getMessage().getEmotes().stream()
						.filter(emote -> emote.canInteract(event.getGuild().getSelfMember()))
						.forEach(emote -> Actions.addReaction(event.getMessage(), emote));
				EmojiParser.extractEmojis(event.getMessage().getContentRaw()).forEach(emoji -> Actions.addReaction(event.getMessage(), emoji));
			}
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("", e);
		}
	}
}
