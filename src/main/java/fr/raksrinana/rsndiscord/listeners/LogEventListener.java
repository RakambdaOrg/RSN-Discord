package fr.raksrinana.rsndiscord.listeners;

import fr.raksrinana.rsndiscord.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateNameEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.Objects;

@EventListener
public class LogEventListener extends ListenerAdapter{
	@Override
	public void onUserUpdateName(@NonNull final UserUpdateNameEvent event){
		super.onUserUpdateName(event);
		try{
			Log.getLogger(null).debug("User {} changed name of {} `{}` to `{}`", event.getUser(), event.getEntity(), event.getOldName(), event.getNewName());
		}
		catch(final NullPointerException ignored){
		}
		catch(final Exception e){
			Log.getLogger(null).error("", e);
		}
	}
	
	@Override
	public void onSelfUpdateName(@NonNull final SelfUpdateNameEvent event){
		super.onSelfUpdateName(event);
		try{
			Log.getLogger(null).debug("User {} changed name `{}` to `{}`", event.getEntity(), event.getOldName(), event.getNewName());
		}
		catch(final NullPointerException ignored){
		}
		catch(final Exception e){
			Log.getLogger(null).error("", e);
		}
	}
	
	@Override
	public void onGuildVoiceGuildMute(@NonNull final GuildVoiceGuildMuteEvent event){
		super.onGuildVoiceGuildMute(event);
		if(Objects.equals(event.getMember().getUser(), event.getJDA().getSelfUser())){
			event.getMember().mute(false).submit();
			event.getMember().deafen(false).submit();
		}
	}
}
