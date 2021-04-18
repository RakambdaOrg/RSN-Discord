package fr.raksrinana.rsndiscord.event;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateNameEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

@EventListener
public class LogEventListener extends ListenerAdapter{
	@Override
	public void onUserUpdateName(@NotNull UserUpdateNameEvent event){
		super.onUserUpdateName(event);
		try{
			Log.getLogger().debug("User {} changed name of {} `{}` to `{}`", event.getUser(), event.getEntity(), event.getOldName(), event.getNewName());
		}
		catch(NullPointerException ignored){
		}
		catch(Exception e){
			Log.getLogger().error("", e);
		}
	}
	
	@Override
	public void onSelfUpdateName(@NotNull SelfUpdateNameEvent event){
		super.onSelfUpdateName(event);
		try{
			Log.getLogger().debug("User {} changed name `{}` to `{}`", event.getEntity(), event.getOldName(), event.getNewName());
		}
		catch(NullPointerException ignored){
		}
		catch(Exception e){
			Log.getLogger().error("", e);
		}
	}
	
	@Override
	public void onGuildVoiceGuildMute(@NotNull GuildVoiceGuildMuteEvent event){
		super.onGuildVoiceGuildMute(event);
		if(Objects.equals(event.getMember().getUser(), event.getJDA().getSelfUser())){
			JDAWrappers.mute(event.getMember(), false).submit();
			JDAWrappers.deafen(event.getMember(), false).submit();
		}
	}
}
