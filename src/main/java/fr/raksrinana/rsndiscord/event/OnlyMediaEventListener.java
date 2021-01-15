package fr.raksrinana.rsndiscord.event;

import fr.raksrinana.rsndiscord.settings.Settings;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static fr.raksrinana.rsndiscord.utils.Utilities.containsChannel;

@EventListener
public class OnlyMediaEventListener extends ListenerAdapter{
	@Override
	public void onGuildMessageReceived(@NonNull GuildMessageReceivedEvent event){
		super.onGuildMessageReceived(event);
		var guild = event.getGuild();
		var message = event.getMessage();
		
		if(containsChannel(Settings.get(guild).getOnlyMediaChannels(), event.getChannel()) && message.getAttachments().isEmpty()){
			message.delete().submit()
					.thenAccept(empty -> event.getAuthor().openPrivateChannel().submit()
							.thenAccept(privateChannel -> privateChannel.sendMessage(translate(guild, "listeners.onlymedia.warn")).submit()));
		}
	}
}
