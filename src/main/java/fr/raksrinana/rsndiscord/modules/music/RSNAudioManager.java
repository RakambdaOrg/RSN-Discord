package fr.raksrinana.rsndiscord.modules.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.music.trackfields.ReplayTrackDataField;
import fr.raksrinana.rsndiscord.modules.music.trackfields.RequesterTrackDataField;
import fr.raksrinana.rsndiscord.modules.music.trackfields.TrackUserFields;
import fr.raksrinana.rsndiscord.modules.settings.Settings;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import static fr.raksrinana.rsndiscord.modules.music.MusicActionResponse.*;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;

public class RSNAudioManager implements IStatusTrackSchedulerListener{
	private static final HashMap<Guild, RSNAudioManager> managers = new HashMap<>();
	@Getter
	private final AudioManager audioManager;
	@Getter
	private final AudioPlayerManager audioPlayerManager;
	@Getter
	private final AudioPlayer audioPlayer;
	@Getter
	private final TrackScheduler trackScheduler;
	@Getter
	private final VoiceChannel channel;
	private boolean isSearchingTracks;
	
	private RSNAudioManager(@NonNull final VoiceChannel channel, @NonNull final AudioManager audioManager, @NonNull final AudioPlayerManager audioPlayerManager, @NonNull final AudioPlayer audioPlayer, @NonNull final TrackScheduler trackScheduler){
		this.channel = channel;
		this.audioManager = audioManager;
		this.audioPlayerManager = audioPlayerManager;
		this.audioPlayer = audioPlayer;
		this.trackScheduler = trackScheduler;
		this.isSearchingTracks = false;
	}
	
	public static void play(@NonNull final User requester, @NonNull final VoiceChannel channel, final @NonNull TrackConsumer listener, int skipCount, int maxTracks, @NonNull final String... identifiers){
		var guild = channel.getGuild();
		var channelName = channel.getName();
		
		var gunterAudioManager = getOrCreatePlayerManager(channel);
		gunterAudioManager.isSearchingTracks = true;
		
		for(var identifier : identifiers){
			gunterAudioManager.getAudioPlayerManager().loadItem(identifier, new AudioLoadResultHandler(){
				@Override
				public void trackLoaded(@NonNull final AudioTrack track){
					Log.getLogger(guild).debug("Added `{}` to the audio queue on channel `{}`", identifier, channelName);
					setTrackData(track, requester);
					try{
						gunterAudioManager.getTrackScheduler().queue(track);
						listener.onTrack(track);
					}
					catch(final Exception e){
						Log.getLogger(guild).warn("Error loading song", e);
					}
					gunterAudioManager.isSearchingTracks = false;
				}
				
				@Override
				public void playlistLoaded(@NonNull final AudioPlaylist playlist){
					Log.getLogger(guild).debug("Added `{}`(size: {}) to the audio queue on channel `{}`", identifier, playlist.getTracks().size(), channelName);
					var tracks = playlist.getTracks().stream()
							.skip(skipCount)
							.limit(maxTracks)
							.collect(toList());
					
					tracks.forEach(track -> {
						Log.getLogger(guild).debug("Added `{}` to the audio queue on channel `{}`", identifier, channelName);
						setTrackData(track, requester);
						try{
							gunterAudioManager.getTrackScheduler().queue(track);
						}
						catch(final Exception e){
							Log.getLogger(guild).warn("Error loading song", e);
						}
					});
					listener.onPlaylist(tracks);
					gunterAudioManager.isSearchingTracks = false;
				}
				
				@Override
				public void noMatches(){
					Log.getLogger(guild).warn("Player found nothing for channel `{}`", channelName);
					gunterAudioManager.isSearchingTracks = false;
					gunterAudioManager.getTrackScheduler().foundNothing();
					listener.onFailure(translate(guild, "music.not-found"));
				}
				
				@Override
				public void loadFailed(@NonNull final FriendlyException throwable){
					Log.getLogger(guild).warn("Failed to load audio for channel `{}`", channelName, throwable);
					gunterAudioManager.isSearchingTracks = false;
					gunterAudioManager.getTrackScheduler().foundNothing();
					listener.onFailure(translate(guild, "music.load-error"));
				}
			});
		}
	}
	
	@NonNull
	private static RSNAudioManager getOrCreatePlayerManager(@NonNull final VoiceChannel channel){
		return managers.computeIfAbsent(channel.getGuild(), g -> createAudioManager(channel));
	}
	
	@NonNull
	private static RSNAudioManager createAudioManager(@NonNull VoiceChannel channel){
		var guild = channel.getGuild();
		
		var audioManager = guild.getAudioManager();
		audioManager.openAudioConnection(channel);
		
		var audioPlayerManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(audioPlayerManager);
		
		var audioPlayer = audioPlayerManager.createPlayer();
		audioManager.setSendingHandler(new AudioPlayerSendHandler(audioPlayer));
		
		var trackScheduler = new TrackScheduler(guild, audioPlayer);
		audioPlayer.setVolume(Math.min(100, Math.max(0, Settings.get(guild).getMusicVolume())));
		audioPlayer.addListener(trackScheduler);
		
		var manager = new RSNAudioManager(channel, audioManager, audioPlayerManager, audioPlayer, trackScheduler);
		trackScheduler.addStatusTrackSchedulerListener(manager);
		
		Log.getLogger(guild).info("Audio manager Created");
		return manager;
	}
	
	private static void setTrackData(@NonNull AudioTrack track, @NonNull final User requester){
		final var userData = new TrackUserFields();
		userData.fill(new RequesterTrackDataField(), requester);
		track.setUserData(userData);
	}
	
	@NonNull
	public static Optional<RSNAudioManager> getFor(@NonNull final Guild guild){
		return ofNullable(managers.get(guild));
	}
	
	public static void shuffle(@NonNull final Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).getTrackScheduler().shuffle();
		}
	}
	
	public static void stopAll(){
		for(var guild : managers.keySet()){
			leave(guild);
		}
	}
	
	@NonNull
	public static MusicActionResponse leave(@NonNull final Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).close();
			return OK;
		}
		return NO_MUSIC;
	}
	
	private void close(){
		if(!this.isSearchingTracks){
			var guild = channel.getGuild();
			var executor = Executors.newScheduledThreadPool(1);
			
			executor.schedule(() -> {
				getAudioPlayerManager().shutdown();
				getAudioManager().setSendingHandler(null);
				
				if(getAudioManager().isConnected()){
					getAudioManager().closeAudioConnection();
				}
				
				managers.remove(guild);
				Log.getLogger(guild).info("Audio manager shutdown");
			}, 2, SECONDS);
			
			executor.shutdown();
		}
	}
	
	@NonNull
	public static List<AudioTrack> getQueue(@NonNull final Guild guild){
		if(managers.containsKey(guild)){
			return managers.get(guild).getTrackScheduler().getQueue();
		}
		return List.of();
	}
	
	@NonNull
	public static MusicActionResponse seek(@NonNull final Guild guild, final long time){
		if(managers.containsKey(guild)){
			var track = currentTrack(guild);
			if(track.isPresent()){
				if(track.get().isSeekable()){
					track.get().setPosition(time);
					return OK;
				}
				
				return IMPOSSIBLE;
			}
		}
		return NO_MUSIC;
	}
	
	@NonNull
	public static Optional<AudioTrack> currentTrack(@NonNull final Guild guild){
		if(managers.containsKey(guild)){
			return ofNullable(managers.get(guild).getAudioPlayer().getPlayingTrack());
		}
		return Optional.empty();
	}
	
	public static boolean isRequester(@NonNull final Guild guild, @NonNull final User user){
		if(managers.containsKey(guild)){
			return currentTrack(guild).map(track -> {
				if(track.getUserData() instanceof TrackUserFields){
					return track.getUserData(TrackUserFields.class)
							.get(new RequesterTrackDataField())
							.map(u -> Objects.equals(user, u))
							.orElse(false);
				}
				return false;
			}).orElse(false);
		}
		return false;
	}
	
	@NonNull
	public static MusicActionResponse pause(@NonNull final Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).getAudioPlayer().setPaused(true);
			return OK;
		}
		return NO_MUSIC;
	}
	
	@NonNull
	public static MusicActionResponse resume(@NonNull final Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).getAudioPlayer().setPaused(false);
			return OK;
		}
		return NO_MUSIC;
	}
	
	@NonNull
	public static MusicActionResponse skip(@NonNull final Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).skip();
			return OK;
		}
		return NO_MUSIC;
	}
	
	private void skip(){
		currentTrack(this.getChannel().getGuild()).ifPresent(track -> {
			if(track.getUserData() instanceof TrackUserFields){
				track.getUserData(TrackUserFields.class).fill(new ReplayTrackDataField(), false);
			}
			this.trackScheduler.skip();
		});
	}
	
	@Override
	public void onTrackSchedulerEmpty(){
		Log.getLogger(this.getChannel().getGuild()).info("Scheduler is empty");
		this.close();
	}
	
	@Override
	public void onTrackEnd(@NonNull final AudioTrack track){
	}
	
	@Override
	public void onTrackStart(@NonNull final AudioTrack track){
	}
	
	public void setVolume(int volume){
		getAudioPlayer().setVolume(Math.min(100, Math.max(0, volume)));
	}
}
