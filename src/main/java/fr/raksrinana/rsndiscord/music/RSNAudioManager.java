package fr.raksrinana.rsndiscord.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.raksrinana.rsndiscord.music.trackfields.ReplayTrackDataField;
import fr.raksrinana.rsndiscord.music.trackfields.RequesterTrackDataField;
import fr.raksrinana.rsndiscord.music.trackfields.TrackUserFields;
import fr.raksrinana.rsndiscord.settings.Settings;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import static fr.raksrinana.rsndiscord.music.MusicActionResponse.*;
import static fr.raksrinana.rsndiscord.utils.LangUtils.translate;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;

@Log4j2
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
	private final AudioChannel channel;
	private boolean isSearchingTracks;
	
	private RSNAudioManager(@NotNull AudioChannel channel, @NotNull AudioManager audioManager, @NotNull AudioPlayerManager audioPlayerManager, @NotNull AudioPlayer audioPlayer, @NotNull TrackScheduler trackScheduler){
		this.channel = channel;
		this.audioManager = audioManager;
		this.audioPlayerManager = audioPlayerManager;
		this.audioPlayer = audioPlayer;
		this.trackScheduler = trackScheduler;
		isSearchingTracks = false;
	}
	
	public static void play(@NotNull User requester, @NotNull AudioChannel channel, @NotNull TrackConsumer listener, int skipCount, int maxTracks, @NotNull String... identifiers){
		var guild = channel.getGuild();
		var channelName = channel.getName();
		
		var gunterAudioManager = getOrCreatePlayerManager(channel);
		gunterAudioManager.isSearchingTracks = true;
		
		for(var identifier : identifiers){
			gunterAudioManager.getAudioPlayerManager().loadItem(identifier, new AudioLoadResultHandler(){
				@Override
				public void trackLoaded(@NotNull AudioTrack track){
					log.debug("Added `{}` to the audio queue on channel `{}`", identifier, channelName);
					setTrackData(track, requester);
					try{
						gunterAudioManager.getTrackScheduler().queue(track);
						listener.onTrack(track);
					}
					catch(Exception e){
						log.warn("Error loading song", e);
					}
					gunterAudioManager.isSearchingTracks = false;
				}
				
				@Override
				public void playlistLoaded(@NotNull AudioPlaylist playlist){
					log.debug("Added `{}`(size: {}) to the audio queue on channel `{}`", identifier, playlist.getTracks().size(), channelName);
					var tracks = playlist.getTracks().stream()
							.skip(skipCount)
							.limit(maxTracks)
							.collect(toList());
					
					tracks.forEach(track -> {
						log.debug("Added `{}` to the audio queue on channel `{}`", identifier, channelName);
						setTrackData(track, requester);
						try{
							gunterAudioManager.getTrackScheduler().queue(track);
						}
						catch(Exception e){
							log.warn("Error loading song", e);
						}
					});
					listener.onPlaylist(tracks);
					gunterAudioManager.isSearchingTracks = false;
				}
				
				@Override
				public void noMatches(){
					log.warn("Player found nothing for channel `{}`", channelName);
					gunterAudioManager.isSearchingTracks = false;
					gunterAudioManager.getTrackScheduler().foundNothing();
					listener.onFailure(translate(guild, "music.not-found"));
				}
				
				@Override
				public void loadFailed(@NotNull FriendlyException throwable){
					log.warn("Failed to load audio for channel `{}`", channelName, throwable);
					gunterAudioManager.isSearchingTracks = false;
					gunterAudioManager.getTrackScheduler().foundNothing();
					listener.onFailure(translate(guild, "music.load-error"));
				}
			});
		}
	}
	
	@NotNull
	private static RSNAudioManager getOrCreatePlayerManager(@NotNull AudioChannel channel){
		return managers.computeIfAbsent(channel.getGuild(), g -> createAudioManager(channel));
	}
	
	private static void setTrackData(@NotNull AudioTrack track, @NotNull User requester){
		var userData = new TrackUserFields();
		userData.fill(new RequesterTrackDataField(), requester);
		track.setUserData(userData);
	}
	
	@NotNull
	private static RSNAudioManager createAudioManager(@NotNull AudioChannel channel){
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
		
		log.info("Audio manager Created");
		return manager;
	}
	
	@NotNull
	public static Optional<RSNAudioManager> getFor(@NotNull Guild guild){
		return ofNullable(managers.get(guild));
	}
	
	public static void shuffle(@NotNull Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).getTrackScheduler().shuffle();
		}
	}
	
	public static void stopAll(){
		for(var guild : managers.keySet()){
			leave(guild);
		}
	}
	
	@NotNull
	public static MusicActionResponse leave(@NotNull Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).close();
			return OK;
		}
		return NO_MUSIC;
	}
	
	private void close(){
		if(!isSearchingTracks){
			var guild = channel.getGuild();
			var executor = Executors.newScheduledThreadPool(1);
			
			executor.schedule(() -> {
				getAudioPlayerManager().shutdown();
				getAudioManager().setSendingHandler(null);
				
				if(getAudioManager().isConnected()){
					getAudioManager().closeAudioConnection();
				}
				
				managers.remove(guild);
				log.info("Audio manager shutdown");
			}, 2, SECONDS);
			
			executor.shutdown();
		}
	}
	
	@NotNull
	public static List<AudioTrack> getQueue(@NotNull Guild guild){
		if(managers.containsKey(guild)){
			return managers.get(guild).getTrackScheduler().getQueue();
		}
		return List.of();
	}
	
	@NotNull
	public static MusicActionResponse seek(@NotNull Guild guild, long time){
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
	
	@NotNull
	public static Optional<AudioTrack> currentTrack(@NotNull Guild guild){
		if(managers.containsKey(guild)){
			return ofNullable(managers.get(guild).getAudioPlayer().getPlayingTrack());
		}
		return Optional.empty();
	}
	
	public static boolean isRequester(@NotNull Guild guild, @NotNull User user){
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
	
	@NotNull
	public static MusicActionResponse pause(@NotNull Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).getAudioPlayer().setPaused(true);
			return OK;
		}
		return NO_MUSIC;
	}
	
	@NotNull
	public static MusicActionResponse resume(@NotNull Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).getAudioPlayer().setPaused(false);
			return OK;
		}
		return NO_MUSIC;
	}
	
	@NotNull
	public static MusicActionResponse skip(@NotNull Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).skip();
			return OK;
		}
		return NO_MUSIC;
	}
	
	private void skip(){
		currentTrack(getChannel().getGuild()).ifPresent(track -> {
			if(track.getUserData() instanceof TrackUserFields){
				track.getUserData(TrackUserFields.class).fill(new ReplayTrackDataField(), false);
			}
			trackScheduler.skip();
		});
	}
	
	@Override
	public void onTrackSchedulerEmpty(){
		log.info("Scheduler is empty");
		close();
	}
	
	@Override
	public void onTrackEnd(@NotNull AudioTrack track){
	}
	
	@Override
	public void onTrackStart(@NotNull AudioTrack track){
	}
	
	public void setVolume(int volume){
		getAudioPlayer().setVolume(Math.min(100, Math.max(0, volume)));
	}
}
