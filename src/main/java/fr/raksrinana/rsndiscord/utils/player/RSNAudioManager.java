package fr.raksrinana.rsndiscord.utils.player;

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
import fr.raksrinana.rsndiscord.utils.player.trackfields.ReplayTrackUserField;
import fr.raksrinana.rsndiscord.utils.player.trackfields.RequesterTrackUserField;
import fr.raksrinana.rsndiscord.utils.player.trackfields.TrackUserFields;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
@SuppressWarnings("WeakerAccess")
public class RSNAudioManager implements StatusTrackSchedulerListener{
	private static final HashMap<Guild, RSNAudioManager> managers = new HashMap<>();
	private final AudioManager audioManager;
	private final AudioPlayerManager audioPlayerManager;
	private final AudioPlayer audioPlayer;
	private final TrackScheduler trackScheduler;
	private final VoiceChannel channel;
	private boolean isSearchingTracks;
	
	private RSNAudioManager(@Nonnull final VoiceChannel channel, @Nonnull final AudioManager audioManager, @Nonnull final AudioPlayerManager audioPlayerManager, @Nonnull final AudioPlayer audioPlayer, @Nonnull final TrackScheduler trackScheduler){
		this.channel = channel;
		this.audioManager = audioManager;
		this.audioPlayerManager = audioPlayerManager;
		this.audioPlayer = audioPlayer;
		this.trackScheduler = trackScheduler;
		this.isSearchingTracks = false;
	}
	
	public static void play(@Nonnull final User requester, @Nonnull final VoiceChannel channel, @Nonnull final String... identifier){
		play(requester, channel, null, identifier);
	}
	
	private static void play(@Nonnull final User requester, @Nonnull final VoiceChannel channel, @SuppressWarnings("SameParameterValue") @Nullable final StatusTrackSchedulerListener listener, @Nonnull final String... identifier){
		play(requester, channel, listener, track -> {}, 0, 10, identifier);
	}
	
	public static void play(@Nonnull final User requester, @Nonnull final VoiceChannel channel, @Nullable final StatusTrackSchedulerListener listener, @Nonnull final Consumer<AudioTrack> onTrackAdded, final int skipCount, final int maxTracks, @Nonnull final String... identifier){
		play(requester, channel, listener, onTrackAdded, playlist -> {}, error -> {}, skipCount, maxTracks, identifier);
	}
	
	public static void play(@Nonnull final User requester, @Nonnull final VoiceChannel channel, @Nullable final StatusTrackSchedulerListener listener, @Nonnull final Consumer<AudioTrack> onTrackAdded, final Consumer<List<AudioTrack>> onPlaylistAdded, final Consumer<String> onFail, final int skipCount, final int maxTracks, @Nonnull final String... identifier){
		final var gunterAudioManager = getGunterPlayerManager(channel, listener);
		gunterAudioManager.isSearchingTracks = true;
		for(final var ident : identifier){
			gunterAudioManager.getAudioPlayerManager().loadItem(ident, new AudioLoadResultHandler(){
				@Override
				public void trackLoaded(@Nonnull final AudioTrack track){
					Log.getLogger(channel.getGuild()).debug("Added `{}` to the audio queue on channel `{}`", ident, channel.getName());
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
				public void playlistLoaded(@Nonnull final AudioPlaylist playlist){
					Log.getLogger(channel.getGuild()).debug("Added `{}`(size: {}) to the audio queue on channel `{}`", ident, playlist.getTracks().size(), channel.getName());
					List<AudioTrack> tracks = playlist.getTracks().stream().skip(skipCount).limit(maxTracks).collect(Collectors.toList());
					tracks.forEach(track -> {
						Log.getLogger(channel.getGuild()).debug("Added `{}` to the audio queue on channel `{}`", ident, channel.getName());
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
				public void loadFailed(@Nonnull final FriendlyException throwable){
					Log.getLogger(channel.getGuild()).warn("Failed to load audio for channel `{}`", channel.getName(), throwable);
					gunterAudioManager.isSearchingTracks = false;
					gunterAudioManager.getTrackScheduler().foundNothing();
					onFail.accept("Error loading music");
				}
			});
		}
	}
	
	@Nonnull
	private static RSNAudioManager getGunterPlayerManager(@Nonnull final VoiceChannel channel, @Nullable final StatusTrackSchedulerListener listener){
		return managers.computeIfAbsent(channel.getGuild(), g -> {
			final var audioManager = channel.getGuild().getAudioManager();
			audioManager.openAudioConnection(channel);
			final AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
			AudioSourceManagers.registerRemoteSources(audioPlayerManager);
			final var audioPlayer = audioPlayerManager.createPlayer();
			audioManager.setSendingHandler(new AudioPlayerSendHandler(audioPlayer));
			final var trackScheduler = new TrackScheduler(channel.getGuild(), audioPlayer);
			audioPlayer.setVolume(Math.min(100, Math.max(0, Settings.getConfiguration(channel.getGuild()).getMusicVolume())));
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
	
	@Nonnull
	private AudioPlayerManager getAudioPlayerManager(){
		return this.audioPlayerManager;
	}
	
	private static void setTrackData(@Nonnull AudioTrack track, @Nonnull final User requester){
		final var userData = new TrackUserFields();
		userData.fill(new RequesterTrackUserField(), requester);
		track.setUserData(userData);
	}
	
	@Nonnull
	private TrackScheduler getTrackScheduler(){
		return this.trackScheduler;
	}
	
	@Nonnull
	public static Optional<RSNAudioManager> getFor(@Nonnull final Guild guild){
		return Optional.ofNullable(managers.get(guild));
	}
	
	public static void shuffle(@Nonnull final Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).getTrackScheduler().shuffle();
		}
	}
	
	public static void stopAll(){
		for(final var guild : managers.keySet()){
			leave(guild);
		}
	}
	
	@Nonnull
	public static MusicActionResponse leave(@Nonnull final Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).close();
			return MusicActionResponse.OK;
		}
		return MusicActionResponse.NO_MUSIC;
	}
	
	@Nonnull
	public static List<AudioTrack> getQueue(@Nonnull final Guild guild){
		if(managers.containsKey(guild)){
			return managers.get(guild).getTrackScheduler().getQueue();
		}
		return List.of();
	}
	
	@Nonnull
	public static MusicActionResponse seek(@Nonnull final Guild guild, final long time){
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
	
	@Nonnull
	public static Optional<AudioTrack> currentTrack(@Nonnull final Guild guild){
		if(managers.containsKey(guild)){
			return Optional.ofNullable(managers.get(guild).getAudioPlayer().getPlayingTrack());
		}
		return Optional.empty();
	}
	
	@Nonnull
	public AudioPlayer getAudioPlayer(){
		return this.audioPlayer;
	}
	
	public static boolean isRequester(@Nonnull final Guild guild, @Nonnull final User user){
		if(managers.containsKey(guild)){
			return currentTrack(guild).map(track -> {
				if(track.getUserData() instanceof TrackUserFields){
					return track.getUserData(TrackUserFields.class).get(new RequesterTrackUserField()).map(u -> Objects.equals(user, u)).orElse(false);
				}
				else{
					return false;
				}
			}).orElse(false);
		}
		return false;
	}
	
	@Nonnull
	public static MusicActionResponse pause(@Nonnull final Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).getAudioPlayer().setPaused(true);
			return MusicActionResponse.OK;
		}
		return MusicActionResponse.NO_MUSIC;
	}
	
	@Nonnull
	public static MusicActionResponse resume(@Nonnull final Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).getAudioPlayer().setPaused(false);
			return MusicActionResponse.OK;
		}
		return MusicActionResponse.NO_MUSIC;
	}
	
	@Nonnull
	public static MusicActionResponse skip(@Nonnull final Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).skip();
			return MusicActionResponse.OK;
		}
		return MusicActionResponse.NO_MUSIC;
	}
	
	private void skip(){
		final var trackOptional = currentTrack(this.getChannel().getGuild());
		trackOptional.ifPresent(track -> {
			if(track.getUserData() instanceof TrackUserFields){
				track.getUserData(TrackUserFields.class).fill(new ReplayTrackUserField(), false);
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
	public void onTrackEnd(@Nonnull final AudioTrack track){
	}
	
	@Override
	public void onTrackStart(@Nonnull final AudioTrack track){
	}
	
	@Nonnull
	public VoiceChannel getChannel(){
		return this.channel;
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
	
	@Nonnull
	private AudioManager getAudioManager(){
		return this.audioManager;
	}
	
	public int getVolume(){
		return getAudioPlayer().getVolume();
	}
	
	public void setVolume(int volume){
		getAudioPlayer().setVolume(Math.min(100, Math.max(0, volume)));
	}
}
