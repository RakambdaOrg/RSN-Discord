package fr.raksrinana.rsndiscord.event;

import fr.raksrinana.rsndiscord.log.LogContext;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateNameEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

@EventListener
@Log4j2
public class LogEventListener extends ListenerAdapter{
	@Override
	public void onUserUpdateName(@NotNull UserUpdateNameEvent event){
		super.onUserUpdateName(event);
		
		var user = event.getUser();
		
		try(var context = LogContext.empty().with(user)){
			log.debug("User {} changed name of {} `{}` to `{}`", user, event.getEntity(), event.getOldName(), event.getNewName());
		}
		catch(NullPointerException ignored){
		}
		catch(Exception e){
			log.error("", e);
		}
	}
	
	@Override
	public void onSelfUpdateName(@NotNull SelfUpdateNameEvent event){
		super.onSelfUpdateName(event);
		
		var user = event.getEntity();
		
		try(var context = LogContext.empty().with(user)){
			log.debug("User {} changed name `{}` to `{}`", user, event.getOldName(), event.getNewName());
		}
		catch(NullPointerException ignored){
		}
		catch(Exception e){
			log.error("", e);
		}
	}
	
	@Override
	public void onGuildVoiceGuildMute(@NotNull GuildVoiceGuildMuteEvent event){
		super.onGuildVoiceGuildMute(event);
		
		try(var context = LogContext.with(event.getGuild())){
			if(Objects.equals(event.getMember().getUser(), event.getJDA().getSelfUser())){
				JDAWrappers.mute(event.getMember(), false).submit();
				JDAWrappers.deafen(event.getMember(), false).submit();
			}
		}
	}
}
