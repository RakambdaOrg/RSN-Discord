package fr.raksrinana.rsndiscord.listeners;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.rsndiscord.utils.player.RSNAudioManager;
import lombok.NonNull;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateNameEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Objects;

public class LogListener extends ListenerAdapter{
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
	public void onGuildMessageReceived(@NonNull final GuildMessageReceivedEvent event){
		super.onGuildMessageReceived(event);
		try{
			if(!event.getAuthor().isBot()){
				final var now = LocalDate.now();
				if(Settings.get(event.getGuild()).getNoXpChannels().stream().noneMatch(c -> Objects.equals(c.getChannelId(), event.getChannel().getIdLong()))){
					final var users = Settings.get(event.getGuild()).getParticipationConfig().getUsers(now);
					users.increment(event.getAuthor().getIdLong(), event.getAuthor().getName());
				}
				final var weekKey = now.minusDays(getDaysToRemove(now.getDayOfWeek()));
				final var emotes = Settings.get(event.getGuild()).getParticipationConfig().getEmotes(weekKey);
				event.getMessage().getEmotes().forEach(emote -> emotes.increment(emote.getIdLong(), emote.getName()));
			}
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("", e);
		}
	}
	
	public static long getDaysToRemove(final DayOfWeek dayOfWeek){
		return switch(dayOfWeek){
			case TUESDAY -> 1;
			case WEDNESDAY -> 2;
			case THURSDAY -> 3;
			case FRIDAY -> 4;
			case SATURDAY -> 5;
			case SUNDAY -> 6;
			default -> 0;
		};
	}
	
	@Override
	public void onGuildVoiceLeave(@NonNull GuildVoiceLeaveEvent event){
		super.onGuildVoiceLeave(event);
		try{
			if(event.getGuild().getAudioManager().isConnected() && event.getChannelLeft().equals(event.getGuild().getAudioManager().getConnectedChannel())){
				if(event.getChannelLeft().getMembers().stream().allMatch(member -> Objects.equals(member.getUser(), event.getJDA().getSelfUser()))){
					Log.getLogger(event.getGuild()).info("The last person left {}, disconnecting", event.getChannelLeft());
					RSNAudioManager.leave(event.getGuild());
				}
			}
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("", e);
		}
	}
	
	@Override
	public void onGuildVoiceGuildMute(@NonNull final GuildVoiceGuildMuteEvent event){
		super.onGuildVoiceGuildMute(event);
		if(Objects.equals(event.getMember().getUser(), event.getJDA().getSelfUser())){
			Actions.mute(event.getMember(), false);
			Actions.deafen(event.getMember(), false);
		}
	}
}
