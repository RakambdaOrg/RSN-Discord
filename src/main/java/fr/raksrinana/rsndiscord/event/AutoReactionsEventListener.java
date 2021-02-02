package fr.raksrinana.rsndiscord.event;

import com.vdurmont.emoji.EmojiParser;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.settings.Settings;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.THUMB_DOWN;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.THUMB_UP;
import static fr.raksrinana.rsndiscord.utils.Utilities.containsChannel;

@EventListener
public class AutoReactionsEventListener extends ListenerAdapter{
	@Override
	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event){
		super.onGuildMessageReceived(event);
		try{
			var guildConfiguration = Settings.get(event.getGuild());
			var message = event.getMessage();
			var channel = event.getChannel();
			
			if(containsChannel(guildConfiguration.getAutoThumbsChannels(), channel)){
				message.addReaction(THUMB_UP.getValue()).submit();
				message.addReaction(THUMB_DOWN.getValue()).submit();
			}
			if(containsChannel(guildConfiguration.getAutoReactionsChannels(), channel)){
				message.getEmotes().stream()
						.filter(emote -> emote.canInteract(event.getGuild().getSelfMember()))
						.forEach(emote -> message.addReaction(emote).submit());
				EmojiParser.extractEmojis(message.getContentRaw())
						.forEach(emoji -> message.addReaction(emoji).submit());
			}
		}
		catch(Exception e){
			Log.getLogger(event.getGuild()).error("", e);
		}
	}
}
