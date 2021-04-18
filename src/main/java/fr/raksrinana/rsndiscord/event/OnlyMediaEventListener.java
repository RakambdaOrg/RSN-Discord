package fr.raksrinana.rsndiscord.event;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static fr.raksrinana.rsndiscord.utils.Utilities.containsChannel;

@EventListener
public class OnlyMediaEventListener extends ListenerAdapter{
	@Override
	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event){
		super.onGuildMessageReceived(event);
		var guild = event.getGuild();
		var message = event.getMessage();
		
		if(containsChannel(Settings.get(guild).getOnlyMediaChannels(), event.getChannel()) && message.getAttachments().isEmpty()){
			JDAWrappers.delete(message).submit()
					.thenAccept(empty -> event.getAuthor().openPrivateChannel().submit()
							.thenAccept(privateChannel -> JDAWrappers.message(privateChannel, translate(guild, "listeners.onlymedia.warn")).submit()));
		}
	}
}
