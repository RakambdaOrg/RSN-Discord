package fr.mrcraftcod.gunterdiscord.listeners;

import net.dv8tion.jda.core.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import static fr.mrcraftcod.gunterdiscord.utils.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-05-06
 */
public class LogListener extends ListenerAdapter{
	@Override
	public void onGuildVoiceGuildMute(GuildVoiceGuildMuteEvent event){
		super.onGuildVoiceGuildMute(event);
		if(event.getMember().getUser().getIdLong() == event.getJDA().getSelfUser().getIdLong()){
			getLogger(event.getGuild()).info("Unmuting bot");
			event.getGuild().getController().setMute(event.getMember(), false).queue();
			event.getGuild().getController().setDeafen(event.getMember(), false).queue();
		}
	}
}
