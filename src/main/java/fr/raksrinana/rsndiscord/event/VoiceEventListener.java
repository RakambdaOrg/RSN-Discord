package fr.raksrinana.rsndiscord.event;

import fr.raksrinana.rsndiscord.log.LogContext;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

@EventListener
@Log4j2
public class VoiceEventListener extends ListenerAdapter{
	@Override
	public void onGuildVoiceGuildMute(@NotNull GuildVoiceGuildMuteEvent event){
		try(var ignored = LogContext.with(event.getGuild())){
			if(Objects.equals(event.getMember().getUser(), event.getJDA().getSelfUser())){
				JDAWrappers.mute(event.getMember(), false).submit();
				JDAWrappers.deafen(event.getMember(), false).submit();
			}
		}
	}
}
