package fr.raksrinana.rsndiscord.event;

import com.vdurmont.emoji.EmojiParser;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
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
				JDAWrappers.addReaction(message, THUMB_UP).submit();
				JDAWrappers.addReaction(message, THUMB_DOWN).submit();
			}
			if(containsChannel(guildConfiguration.getAutoReactionsChannels(), channel)){
				message.getEmotes().stream()
						.filter(emote -> emote.canInteract(event.getGuild().getSelfMember()))
						.forEach(emote -> JDAWrappers.addReaction(message, emote).submit());
				EmojiParser.extractEmojis(message.getContentRaw())
						.forEach(emoji -> JDAWrappers.addReaction(message, emoji).submit());
			}
		}
		catch(Exception e){
			Log.getLogger(event.getGuild()).error("", e);
		}
	}
}
