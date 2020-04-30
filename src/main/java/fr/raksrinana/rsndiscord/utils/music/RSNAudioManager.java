package fr.raksrinana.rsndiscord.utils.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.raksrinana.rsndiscord.settings.Settings;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.rsndiscord.utils.music.trackfields.ReplayTrackDataField;
import fr.raksrinana.rsndiscord.utils.music.trackfields.RequesterTrackDataField;
import fr.raksrinana.rsndiscord.utils.music.trackfields.TrackUserFields;
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
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RSNAudioManager implements StatusTrackSchedulerListener{
	private static final HashMap<Guild, RSNAudioManager> managers = new HashMap<>();
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
	
	public static void play(@NonNull final User requester, @NonNull final VoiceChannel channel, @NonNull final String... identifier){
		play(requester, channel, null, identifier);
	}
	
	private static void play(@NonNull final User requester, @NonNull final VoiceChannel channel, @SuppressWarnings("SameParameterValue") final StatusTrackSchedulerListener listener, @NonNull final String... identifier){
		play(requester, channel, listener, track -> {}, 0, 10, identifier);
	}
	
	public static void play(@NonNull final User requester, @NonNull final VoiceChannel channel, final StatusTrackSchedulerListener listener, @NonNull final Consumer<AudioTrack> onTrackAdded, final int skipCount, final int maxTracks, @NonNull final String... identifier){
		play(requester, channel, listener, onTrackAdded, playlist -> {}, error -> {}, skipCount, maxTracks, identifier);
	}
	
	public static void play(@NonNull final User requester, @NonNull final VoiceChannel channel, final StatusTrackSchedulerListener listener, @NonNull final Consumer<AudioTrack> onTrackAdded, final Consumer<List<AudioTrack>> onPlaylistAdded, final Consumer<String> onFail, final int skipCount, final int maxTracks, @NonNull final String... identifiers){
		final var gunterAudioManager = getOrCreatePlayerManager(channel, listener);
		gunterAudioManager.isSearchingTracks = true;
		for(final var identifier : identifiers){
			gunterAudioManager.getAudioPlayerManager().loadItem(identifier, new AudioLoadResultHandler(){
				@Override
				public void trackLoaded(@NonNull final AudioTrack track){
					Log.getLogger(channel.getGuild()).debug("Added `{}` to the audio queue on channel `{}`", identifier, channel.getName());
					setTrackData(track, requester);
					try{
						gunterAudioManager.getTrackScheduler().queue(track);
						onTrackAdded.accept(track);
					}
					catch(final Exception e){
						Log.getLogger(channel.getGuild()).warn("Error loading song", e);
					}
					gunterAudioManager.isSearchingTracks = false;
				}
				
				@Override
				public void playlistLoaded(@NonNull final AudioPlaylist playlist){
					Log.getLogger(channel.getGuild()).debug("Added `{}`(size: {}) to the audio queue on channel `{}`", identifier, playlist.getTracks().size(), channel.getName());
					List<AudioTrack> tracks = playlist.getTracks().stream().skip(skipCount).limit(maxTracks).collect(Collectors.toList());
					tracks.forEach(track -> {
						Log.getLogger(channel.getGuild()).debug("Added `{}` to the audio queue on channel `{}`", identifier, channel.getName());
						setTrackData(track, requester);
						try{
							gunterAudioManager.getTrackScheduler().queue(track);
						}
						catch(final Exception e){
							Log.getLogger(channel.getGuild()).warn("Error loading song", e);
						}
					});
					onPlaylistAdded.accept(tracks);
					gunterAudioManager.isSearchingTracks = false;
				}
				
				@Override
				public void noMatches(){
					Log.getLogger(channel.getGuild()).warn("Player found nothing for channel `{}`", channel.getName());
					gunterAudioManager.isSearchingTracks = false;
					gunterAudioManager.getTrackScheduler().foundNothing();
					onFail.accept("No music found");
				}
				
				@Override
				public void loadFailed(@NonNull final FriendlyException throwable){
					Log.getLogger(channel.getGuild()).warn("Failed to load audio for channel `{}`", channel.getName(), throwable);
					gunterAudioManager.isSearchingTracks = false;
					gunterAudioManager.getTrackScheduler().foundNothing();
					onFail.accept("Error loading music");
				}
			});
		}
	}
	
	@NonNull
	private static RSNAudioManager getOrCreatePlayerManager(@NonNull final VoiceChannel channel, final StatusTrackSchedulerListener listener){
		return managers.computeIfAbsent(channel.getGuild(), g -> {
			final var audioManager = channel.getGuild().getAudioManager();
			audioManager.openAudioConnection(channel);
			final AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
			AudioSourceManagers.registerRemoteSources(audioPlayerManager);
			final var audioPlayer = audioPlayerManager.createPlayer();
			audioManager.setSendingHandler(new AudioPlayerSendHandler(audioPlayer));
			final var trackScheduler = new TrackScheduler(channel.getGuild(), audioPlayer);
			audioPlayer.setVolume(Math.min(100, Math.max(0, Settings.get(channel.getGuild()).getMusicVolume())));
			audioPlayer.addListener(trackScheduler);
			final var gunterAudioManager = new RSNAudioManager(channel, audioManager, audioPlayerManager, audioPlayer, trackScheduler);
			trackScheduler.addStatusTrackSchedulerListener(gunterAudioManager);
			if(Objects.nonNull(listener)){
				trackScheduler.addStatusTrackSchedulerListener(listener);
			}
			Log.getLogger(channel.getGuild()).info("Audio manager Created");
			return gunterAudioManager;
		});
	}
	
	private static void setTrackData(@NonNull AudioTrack track, @NonNull final User requester){
		final var userData = new TrackUserFields();
		userData.fill(new RequesterTrackDataField(), requester);
		track.setUserData(userData);
	}
	
	@NonNull
	public static Optional<RSNAudioManager> getFor(@NonNull final Guild guild){
		return Optional.ofNullable(managers.get(guild));
	}
	
	public static void shuffle(@NonNull final Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).getTrackScheduler().shuffle();
		}
	}
	
	public static void stopAll(){
		for(final var guild : managers.keySet()){
			leave(guild);
		}
	}
	
	@NonNull
	public static MusicActionResponse leave(@NonNull final Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).close();
			return MusicActionResponse.OK;
		}
		return MusicActionResponse.NO_MUSIC;
	}
	
	private void close(){
		if(!this.isSearchingTracks){
			final var executor = Executors.newScheduledThreadPool(1);
			executor.schedule(() -> {
				this.getAudioPlayerManager().shutdown();
				this.getAudioManager().setSendingHandler(null);
				if(this.getAudioManager().isConnected()){
					this.getAudioManager().closeAudioConnection();
				}
				managers.remove(this.channel.getGuild());
				Log.getLogger(this.getChannel().getGuild()).info("Audio manager shutdown");
			}, 2, TimeUnit.SECONDS);
			executor.shutdown();
		}
	}
	
	@NonNull
	private AudioManager getAudioManager(){
		return this.audioManager;
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
			final var track = currentTrack(guild);
			if(track.isPresent()){
				if(track.get().isSeekable()){
					track.get().setPosition(time);
					return MusicActionResponse.OK;
				}
				else{
					return MusicActionResponse.IMPOSSIBLE;
				}
			}
		}
		return MusicActionResponse.NO_MUSIC;
	}
	
	@NonNull
	public static Optional<AudioTrack> currentTrack(@NonNull final Guild guild){
		if(managers.containsKey(guild)){
			return Optional.ofNullable(managers.get(guild).getAudioPlayer().getPlayingTrack());
		}
		return Optional.empty();
	}
	
	public static boolean isRequester(@NonNull final Guild guild, @NonNull final User user){
		if(managers.containsKey(guild)){
			return currentTrack(guild).map(track -> {
				if(track.getUserData() instanceof TrackUserFields){
					return track.getUserData(TrackUserFields.class).get(new RequesterTrackDataField()).map(u -> Objects.equals(user, u)).orElse(false);
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
			return MusicActionResponse.OK;
		}
		return MusicActionResponse.NO_MUSIC;
	}
	
	@NonNull
	public static MusicActionResponse resume(@NonNull final Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).getAudioPlayer().setPaused(false);
			return MusicActionResponse.OK;
		}
		return MusicActionResponse.NO_MUSIC;
	}
	
	@NonNull
	public static MusicActionResponse skip(@NonNull final Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).skip();
			return MusicActionResponse.OK;
		}
		return MusicActionResponse.NO_MUSIC;
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
	
	public int getVolume(){
		return getAudioPlayer().getVolume();
	}
	
	public void setVolume(int volume){
		getAudioPlayer().setVolume(Math.min(100, Math.max(0, volume)));
	}
}
