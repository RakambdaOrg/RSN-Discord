package fr.raksrinana.rsndiscord.modules.participation.listener;

import fr.raksrinana.rsndiscord.listeners.EventListener;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.music.RSNAudioManager;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import static java.time.Duration.between;
import static java.time.ZoneOffset.UTC;

@EventListener
public class ParticipationEventListener extends ListenerAdapter{
	private final Map<Long, Map<Long, ZonedDateTime>> lastVocalJoin;
	
	public ParticipationEventListener(){
		lastVocalJoin = new HashMap<>();
	}
	
	@Override
	public void onGuildMessageReceived(@NonNull GuildMessageReceivedEvent event){
		super.onGuildMessageReceived(event);
		var guild = event.getGuild();
		var author = event.getAuthor();
		
		try{
			if(author.isBot() || event.isWebhookMessage()){
				return;
			}
			
			var participationConfiguration = Settings.get(guild).getParticipationConfiguration();
			if(!participationConfiguration.isChannelIgnored(event.getChannel())){
				participationConfiguration.getOrCreateChatDay(LocalDate.now(UTC))
						.incrementUser(author);
			}
		}
		catch(final Exception e){
			Log.getLogger(guild).error("", e);
		}
	}
	
	@Override
	public void onGuildVoiceJoin(@NonNull GuildVoiceJoinEvent event){
		super.onGuildVoiceJoin(event);
		try{
			var user = event.getMember().getUser();
			
			if(!user.isBot()){
				userJoinedVoice(user, event.getChannelJoined());
			}
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("", e);
		}
	}
	
	private void userJoinedVoice(@NonNull User user, @NonNull VoiceChannel channel){
		var participationConfiguration = Settings.get(channel.getGuild()).getParticipationConfiguration();
		if(!participationConfiguration.isChannelIgnored(channel)){
			this.lastVocalJoin.computeIfAbsent(channel.getIdLong(), channelId -> new HashMap<>())
					.put(user.getIdLong(), ZonedDateTime.now());
		}
	}
	
	@Override
	public void onGuildVoiceMove(@NonNull GuildVoiceMoveEvent event){
		super.onGuildVoiceMove(event);
		try{
			var user = event.getMember().getUser();
			
			if(!user.isBot()){
				userLeftVoice(user, event.getChannelLeft());
				userJoinedVoice(user, event.getChannelJoined());
			}
		}
		catch(final Exception e){
			Log.getLogger(event.getGuild()).error("", e);
		}
	}
	
	@Override
	public void onGuildVoiceLeave(@NonNull GuildVoiceLeaveEvent event){
		super.onGuildVoiceLeave(event);
		var guild = event.getGuild();
		
		try{
			var user = event.getMember().getUser();
			var channelLeft = event.getChannelLeft();
			
			if(Objects.equals(channelLeft, guild.getAudioManager().getConnectedChannel())){
				var allUsersLeft = channelLeft.getMembers().stream()
						.allMatch(member -> Objects.equals(member.getUser(), event.getJDA().getSelfUser()));
				if(allUsersLeft){
					Log.getLogger(guild).info("The last person left {}, disconnecting", channelLeft);
					RSNAudioManager.leave(guild);
				}
			}
			
			if(!user.isBot()){
				userLeftVoice(user, channelLeft);
			}
		}
		catch(final Exception e){
			Log.getLogger(guild).error("", e);
		}
	}
	
	private void userLeftVoice(@NonNull User user, @NonNull VoiceChannel channel){
		final var participationConfiguration = Settings.get(channel.getGuild()).getParticipationConfiguration();
		if(!participationConfiguration.isChannelIgnored(channel)){
			Optional.ofNullable(this.lastVocalJoin.get(channel.getIdLong()))
					.map(map -> map.get(user.getIdLong()))
					.ifPresent(joinDate -> {
						var timeSpent = between(joinDate, ZonedDateTime.now()).toMinutes();
						participationConfiguration.getOrCreateVoiceDay(LocalDate.now(UTC))
								.incrementUser(user, timeSpent);
					});
		}
	}
}
