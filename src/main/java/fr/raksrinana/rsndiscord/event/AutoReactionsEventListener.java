package fr.raksrinana.rsndiscord.event;

import fr.raksrinana.rsndiscord.log.LogContext;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.THUMB_DOWN;
import static fr.raksrinana.rsndiscord.utils.BasicEmotes.THUMB_UP;
import static fr.raksrinana.rsndiscord.utils.Utilities.containsChannel;

@EventListener
@Log4j2
public class AutoReactionsEventListener extends ListenerAdapter{
	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event){
		super.onMessageReceived(event);
		if(!event.isFromGuild()){
			return;
		}
		
		var guild = event.getGuild();
		try(var ignored = LogContext.with(guild).with(event.getAuthor())){
			var guildConfiguration = Settings.get(guild);
			var message = event.getMessage();
			var channel = event.getChannel();
			
			if(containsChannel(guildConfiguration.getAutoThumbsChannels(), channel)){
				JDAWrappers.addReaction(message, THUMB_UP).submit();
				JDAWrappers.addReaction(message, THUMB_DOWN).submit();
			}
		}
		catch(Exception e){
			log.error("", e);
		}
	}
}
