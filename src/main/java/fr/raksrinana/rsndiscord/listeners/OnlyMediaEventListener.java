package fr.raksrinana.rsndiscord.listeners;

import fr.raksrinana.rsndiscord.modules.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import lombok.NonNull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.Objects;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;

@EventListener
public class OnlyMediaEventListener extends ListenerAdapter{
	@Override
	public void onGuildMessageReceived(@NonNull GuildMessageReceivedEvent event){
		super.onGuildMessageReceived(event);
		if(Settings.get(event.getGuild())
				.getOnlyMediaChannels().stream()
				.anyMatch(channelConfiguration -> Objects.equals(channelConfiguration.getChannelId(), event.getChannel().getIdLong()))){
			if(event.getMessage().getAttachments().isEmpty()){
				Actions.deleteMessage(event.getMessage())
						.thenAccept(empty -> Actions.sendPrivateMessage(event.getGuild(), event.getAuthor(), translate(event.getGuild(), "listeners.onlymedia.warn"), null));
			}
		}
	}
}
