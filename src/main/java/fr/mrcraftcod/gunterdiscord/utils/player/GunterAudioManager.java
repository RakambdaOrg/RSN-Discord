package fr.mrcraftcod.gunterdiscord.utils.player;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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
	
	private GunterAudioManager(VoiceChannel channel, AudioManager audioManager, AudioPlayerManager audioPlayerManager, AudioPlayer audioPlayer, TrackScheduler trackScheduler){
		this.channel = channel;
		this.audioManager = audioManager;
		this.audioPlayerManager = audioPlayerManager;
		this.audioPlayer = audioPlayer;
		this.trackScheduler = trackScheduler;
	}
	
	public static void play(VoiceChannel channel, String... identifier){
		play(channel, null, identifier);
	}
	
	private static void play(VoiceChannel channel, StatusTrackSchedulerListener listener, String... identifier){
		play(channel, listener, track -> {}, identifier);
	}
	
	public static void play(VoiceChannel channel, StatusTrackSchedulerListener listener, Consumer<AudioTrack> onTrackAdded, String... identifier){
		GunterAudioManager gunterAudioManager = getGunterPlayerManager(channel, listener);
		for(String ident : identifier){
			gunterAudioManager.getAudioPlayerManager().loadItem(ident, new AudioLoadResultHandler(){
				@Override
				public void trackLoaded(AudioTrack track){
					Log.info(channel.getGuild(), "Added `{}` to the audio queue on channel `{}`", ident, channel.getName());
					gunterAudioManager.getTrackScheduler().queue(track);
					onTrackAdded.accept(track);
				}
				
				@Override
				public void playlistLoaded(AudioPlaylist playlist){
					Log.info(channel.getGuild(), "Added `{}`({}) to the audio queue on channel `{}`", ident, playlist.getTracks().size(), channel.getName());
					for(AudioTrack track : playlist.getTracks()){
						gunterAudioManager.getTrackScheduler().queue(track);
						onTrackAdded.accept(track);
					}
				}
				
				@Override
				public void noMatches(){
					Log.warning(channel.getGuild(), "Player found nothing for channel `{}`", channel.getName());
					gunterAudioManager.getTrackScheduler().foundNothing();
				}
				
				@Override
				public void loadFailed(FriendlyException throwable){
					Log.warning(channel.getGuild(), "Failed to load audio for channel `{}`", channel.getName(), throwable);
					gunterAudioManager.getTrackScheduler().foundNothing();
				}
			});
		}
	}
	
	private static GunterAudioManager getGunterPlayerManager(VoiceChannel channel, StatusTrackSchedulerListener listener){
		return managers.computeIfAbsent(channel.getGuild(), g -> {
			AudioManager audioManager = channel.getGuild().getAudioManager();
			audioManager.openAudioConnection(channel);
			AudioPlayerManager audioPlayerManager = new DefaultAudioPlayerManager();
			AudioSourceManagers.registerRemoteSources(audioPlayerManager);
			AudioPlayer audioPlayer = audioPlayerManager.createPlayer();
			audioManager.setSendingHandler(new AudioPlayerSendHandler(audioPlayer));
			TrackScheduler trackScheduler = new TrackScheduler(channel.getGuild(), audioPlayer);
			audioPlayer.addListener(trackScheduler);
			GunterAudioManager gunterAudioManager = new GunterAudioManager(channel, audioManager, audioPlayerManager, audioPlayer, trackScheduler);
			trackScheduler.addStatusTrackSchedulerListener(gunterAudioManager);
			if(listener != null){
				trackScheduler.addStatusTrackSchedulerListener(listener);
			}
			Log.info(channel.getGuild(), "Audio manager Created");
			return gunterAudioManager;
		});
	}
	
	private AudioPlayerManager getAudioPlayerManager(){
		return audioPlayerManager;
	}
	
	private TrackScheduler getTrackScheduler(){
		return trackScheduler;
	}
	
	public static void skip(Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).skip();
		}
	}
	
	private void skip(){
		trackScheduler.nextTrack();
	}
	
	public static void leave(Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).onTrackSchedulerEmpty();
		}
	}
	
	@Override
	public void onTrackSchedulerEmpty(){
		Log.info(getChannel().getGuild(), "Scheduler is empty");
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.schedule(() -> {
			getAudioPlayerManager().shutdown();
			getAudioManager().setSendingHandler(null);
			getAudioManager().closeAudioConnection();
			managers.remove(channel.getGuild());
			Log.info(getChannel().getGuild(), "Audio manager shutdown");
		}, 1, TimeUnit.NANOSECONDS);
		executorService.shutdown();
	}
	
	@Override
	public void onTrackEnd(AudioTrack track){
	}
	
	@Override
	public void onTrackStart(AudioTrack track){
	}
	
	private VoiceChannel getChannel(){
		return channel;
	}
	
	private AudioManager getAudioManager(){
		return audioManager;
	}
	
	public void addListener(StatusTrackSchedulerListener listener){
		trackScheduler.addStatusTrackSchedulerListener(listener);
	}
	
	public AudioPlayer getAudioPlayer(){
		return audioPlayer;
	}
}
