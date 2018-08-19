package fr.mrcraftcod.gunterdiscord.utils.player;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
public class GunterAudioManager implements StatusTrackSchedulerListener{
	private static final HashMap<Guild, GunterAudioManager> managers = new HashMap<>();
	private final AudioManager audioManager;
	private final AudioPlayerManager audioPlayerManager;
	private final AudioPlayer audioPlayer;
	private final TrackScheduler trackScheduler;
	private final VoiceChannel channel;
	
	private GunterAudioManager(final VoiceChannel channel, final AudioManager audioManager, final AudioPlayerManager audioPlayerManager, final AudioPlayer audioPlayer, final TrackScheduler trackScheduler){
		this.channel = channel;
		this.audioManager = audioManager;
		this.audioPlayerManager = audioPlayerManager;
		this.audioPlayer = audioPlayer;
		this.trackScheduler = trackScheduler;
	}
	
	public static void play(final VoiceChannel channel, final String... identifier){
		play(channel, null, identifier);
	}
	
	private static void play(final VoiceChannel channel, final StatusTrackSchedulerListener listener, final String... identifier){
		play(channel, listener, track -> {}, identifier);
	}
	
	public static void play(final VoiceChannel channel, final StatusTrackSchedulerListener listener, final Consumer<AudioTrack> onTrackAdded, final String... identifier){
		final var gunterAudioManager = getGunterPlayerManager(channel, listener);
		for(final var ident : identifier){
			gunterAudioManager.getAudioPlayerManager().loadItem(ident, new AudioLoadResultHandler(){
				@Override
				public void trackLoaded(final AudioTrack track){
					getLogger(channel.getGuild()).info("Added `{}` to the audio queue on channel `{}`", ident, channel.getName());
					gunterAudioManager.getTrackScheduler().queue(track);
					onTrackAdded.accept(track);
				}
				
				@Override
				public void playlistLoaded(final AudioPlaylist playlist){
					getLogger(channel.getGuild()).info("Added `{}`({}) to the audio queue on channel `{}`", ident, playlist.getTracks().size(), channel.getName());
					for(final var track : playlist.getTracks()){
						gunterAudioManager.getTrackScheduler().queue(track);
						onTrackAdded.accept(track);
					}
				}
				
				@Override
				public void noMatches(){
					getLogger(channel.getGuild()).warn("Player found nothing for channel `{}`", channel.getName());
					gunterAudioManager.getTrackScheduler().foundNothing();
					onTrackAdded.accept(null);
				}
				
				@Override
				public void loadFailed(final FriendlyException throwable){
					getLogger(channel.getGuild()).warn("Failed to load audio for channel `{}`", channel.getName(), throwable);
					gunterAudioManager.getTrackScheduler().foundNothing();
					onTrackAdded.accept(null);
				}
			});
		}
	}
	
	private static GunterAudioManager getGunterPlayerManager(final VoiceChannel channel, final StatusTrackSchedulerListener listener){
		return managers.computeIfAbsent(channel.getGuild(), g -> {
			final var audioManager = channel.getGuild().getAudioManager();
			audioManager.openAudioConnection(channel);
			final AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
			AudioSourceManagers.registerRemoteSources(audioPlayerManager);
			final var audioPlayer = audioPlayerManager.createPlayer();
			audioManager.setSendingHandler(new AudioPlayerSendHandler(audioPlayer));
			final var trackScheduler = new TrackScheduler(channel.getGuild(), audioPlayer);
			audioPlayer.addListener(trackScheduler);
			final var gunterAudioManager = new GunterAudioManager(channel, audioManager, audioPlayerManager, audioPlayer, trackScheduler);
			trackScheduler.addStatusTrackSchedulerListener(gunterAudioManager);
			if(listener != null){
				trackScheduler.addStatusTrackSchedulerListener(listener);
			}
			getLogger(channel.getGuild()).info("Audio manager Created");
			return gunterAudioManager;
		});
	}
	
	public static void seek(Guild guild, long time){
		if(managers.containsKey(guild)){
			var track = currentTrack(guild).orElseThrow(() -> new IllegalStateException("Aucune musique ne joue"));
			if(track.isSeekable()){
				managers.get(guild).getAudioPlayer().getPlayingTrack().setPosition(time);
			}
			else{
				throw new IllegalStateException("La musique ne peut pas être modifiée");
			}
		}
	}
	
	public AudioPlayer getAudioPlayer(){
		return audioPlayer;
	}
	
	public static void pause(final Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).getAudioPlayer().setPaused(true);
		}
	}
	
	private AudioPlayerManager getAudioPlayerManager(){
		return audioPlayerManager;
	}
	
	public static void resume(final Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).getAudioPlayer().setPaused(false);
		}
	}
	
	private TrackScheduler getTrackScheduler(){
		return trackScheduler;
	}
	
	public static Optional<AudioTrack> currentTrack(Guild guild){
		if(managers.containsKey(guild)){
			return Optional.ofNullable(managers.get(guild).getAudioPlayer().getPlayingTrack());
		}
		return Optional.empty();
	}
	
	public static void skip(final Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).skip();
		}
	}
	
	private void skip(){
		trackScheduler.nextTrack();
	}
	
	public static void leave(final Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).onTrackSchedulerEmpty();
		}
	}
	
	@Override
	public void onTrackSchedulerEmpty(){
		getLogger(getChannel().getGuild()).info("Scheduler is empty");
		final var executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.schedule(() -> {
			getAudioPlayerManager().shutdown();
			getAudioManager().setSendingHandler(null);
			getAudioManager().closeAudioConnection();
			managers.remove(channel.getGuild());
			getLogger(getChannel().getGuild()).info("Audio manager shutdown");
		}, 1, TimeUnit.NANOSECONDS);
		executorService.shutdown();
	}
	
	@Override
	public void onTrackEnd(final AudioTrack track){
	}
	
	@Override
	public void onTrackStart(final AudioTrack track){
	}
	
	private VoiceChannel getChannel(){
		return channel;
	}
	
	private AudioManager getAudioManager(){
		return audioManager;
	}
	
	public void addListener(final StatusTrackSchedulerListener listener){
		trackScheduler.addStatusTrackSchedulerListener(listener);
	}
}
