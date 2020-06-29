package fr.raksrinana.rsndiscord.listeners;

import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.settings.guild.leavingroles.LeaverRoles;
import fr.raksrinana.rsndiscord.settings.types.RoleConfiguration;
import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.rsndiscord.utils.music.RSNAudioManager;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateNameEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import static java.time.ZoneOffset.UTC;

public class LogListener extends ListenerAdapter{
	private final Map<Long, Map<Long, ZonedDateTime>> lastVocalJoin;
	
	public LogListener(){
		lastVocalJoin = new HashMap<>();
	}
	
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
	public void onGuildMessageReceived(@NonNull GuildMessageReceivedEvent event){
		super.onGuildMessageReceived(event);
		try{
			if(!event.getAuthor().isBot() && !event.isWebhookMessage()){
				final var participationConfiguration = Settings.get(event.getGuild()).getParticipationConfiguration();
				if(!participationConfiguration.isChannelIgnored(event.getChannel())){
					participationConfiguration.getOrCreateChatDay(LocalDate.now(UTC)).incrementUser(event.getAuthor());
				}
			}
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("", e);
		}
	}
	
	@Override
	public void onGuildVoiceJoin(@NonNull GuildVoiceJoinEvent event){
		super.onGuildVoiceJoin(event);
		try{
			if(!event.getMember().getUser().isBot()){
				userJoinedVoice(event.getMember().getUser(), event.getChannelJoined());
			}
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("", e);
		}
	}
	
	private void userJoinedVoice(@NonNull User user, @NonNull VoiceChannel channel){
		final var participationConfiguration = Settings.get(channel.getGuild()).getParticipationConfiguration();
		if(!participationConfiguration.isChannelIgnored(channel)){
			this.lastVocalJoin.computeIfAbsent(channel.getIdLong(), channelId -> new HashMap<>())
					.put(user.getIdLong(), ZonedDateTime.now());
		}
	}
	
	@Override
	public void onGuildVoiceMove(@NonNull GuildVoiceMoveEvent event){
		super.onGuildVoiceMove(event);
		try{
			if(!event.getMember().getUser().isBot()){
				userLeftVoice(event.getMember().getUser(), event.getChannelLeft());
				userJoinedVoice(event.getMember().getUser(), event.getChannelJoined());
			}
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("", e);
		}
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
			if(!event.getMember().getUser().isBot()){
				userLeftVoice(event.getMember().getUser(), event.getChannelLeft());
			}
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("", e);
		}
	}
	
	private void userLeftVoice(@NonNull User user, @NonNull VoiceChannel channel){
		final var participationConfiguration = Settings.get(channel.getGuild()).getParticipationConfiguration();
		if(!participationConfiguration.isChannelIgnored(channel)){
			Optional.ofNullable(this.lastVocalJoin.get(channel.getIdLong()))
					.map(map -> map.get(user.getIdLong()))
					.ifPresent(joinDate -> {
						var timeSpent = Duration.between(joinDate, ZonedDateTime.now()).toMinutes();
						participationConfiguration.getOrCreateVoiceDay(LocalDate.now(UTC)).incrementUser(user, timeSpent);
					});
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
	
	@Override
	public void onGuildMemberRemove(@NonNull GuildMemberRemoveEvent event){
		super.onGuildMemberRemove(event);
		Optional.ofNullable(event.getMember()).ifPresent(member -> {
			var leavingRolesConfiguration = Settings.get(event.getGuild()).getLeavingRolesConfiguration();
			leavingRolesConfiguration.addLeaver(new LeaverRoles(event.getUser(), member.getRoles()));
		});
	}
	
	@Override
	public void onGuildMemberJoin(@NonNull GuildMemberJoinEvent event){
		super.onGuildMemberJoin(event);
		var leavingRolesConfiguration = Settings.get(event.getGuild()).getLeavingRolesConfiguration();
		leavingRolesConfiguration.getLeaver(event.getUser()).ifPresent(leaverRoles -> {
			leaverRoles.getRoles().stream()
					.map(RoleConfiguration::getRole)
					.map(Optional::get)
					.forEach(role -> Actions.giveRole(event.getMember(), role));
			leavingRolesConfiguration.removeUser(event.getUser());
		});
	}
}
