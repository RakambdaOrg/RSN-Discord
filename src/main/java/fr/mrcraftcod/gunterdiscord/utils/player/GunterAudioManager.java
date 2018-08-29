package fr.mrcraftcod.gunterdiscord.utils.player;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.mrcraftcod.gunterdiscord.utils.player.sourcemanagers.httpfolder.HttpFolderAudioSourceManager;
import fr.mrcraftcod.gunterdiscord.utils.player.trackfields.RequesterTrackUserField;
import fr.mrcraftcod.gunterdiscord.utils.player.trackfields.TrackUserFields;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;
import static fr.mrcraftcod.gunterdiscord.utils.player.MusicActionResponse.*;

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
	
	public static void play(final User requester, final VoiceChannel channel, final String... identifier){
		play(requester, channel, null, identifier);
	}
	
	private static void play(final User requester, final VoiceChannel channel, final StatusTrackSchedulerListener listener, final String... identifier){
		play(requester, channel, listener, track -> {}, 0, 10, identifier);
	}
	
	public static void play(final User requester, final VoiceChannel channel, final StatusTrackSchedulerListener listener, final Consumer<Object> onTrackAdded, final int skipCount, final int maxTracks, final String... identifier){
		final var gunterAudioManager = getGunterPlayerManager(channel, listener);
		for(final var ident : identifier){
			gunterAudioManager.getAudioPlayerManager().loadItem(ident, new AudioLoadResultHandler(){
				@Override
				public void trackLoaded(final AudioTrack track){
					getLogger(channel.getGuild()).info("Added `{}` to the audio queue on channel `{}`", ident, channel.getName());
					final var userData = new TrackUserFields();
					userData.fill(new RequesterTrackUserField(), requester);
					track.setUserData(userData);
					gunterAudioManager.getTrackScheduler().queue(track);
					onTrackAdded.accept(track);
				}
				
				@Override
				public void playlistLoaded(final AudioPlaylist playlist){
					getLogger(channel.getGuild()).info("Added `{}`({}) to the audio queue on channel `{}`", ident, playlist.getTracks().size(), channel.getName());
					final var userData = new TrackUserFields();
					userData.fill(new RequesterTrackUserField(), requester);
					playlist.getTracks().stream().skip(skipCount).limit(maxTracks).forEach(track -> {
						track.setUserData(userData);
						gunterAudioManager.getTrackScheduler().queue(track);
						onTrackAdded.accept(track);
					});
				}
				
				@Override
				public void noMatches(){
					getLogger(channel.getGuild()).warn("Player found nothing for channel `{}`", channel.getName());
					gunterAudioManager.getTrackScheduler().foundNothing();
					onTrackAdded.accept("Aucune musique trouvée");
				}
				
				@Override
				public void loadFailed(final FriendlyException throwable){
					getLogger(channel.getGuild()).warn("Failed to load audio for channel `{}`", channel.getName(), throwable);
					gunterAudioManager.getTrackScheduler().foundNothing();
					onTrackAdded.accept("Erreur pendant le chargement");
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
			audioPlayerManager.registerSourceManager(new HttpFolderAudioSourceManager());
			getLogger(channel.getGuild()).info("Audio manager Created");
			return gunterAudioManager;
		});
	}
	
	private AudioPlayerManager getAudioPlayerManager(){
		return audioPlayerManager;
	}
	
	private TrackScheduler getTrackScheduler(){
		return trackScheduler;
	}
	
	public static Collection<AudioTrack> getQueue(Guild guild){
		if(managers.containsKey(guild)){
			return managers.get(guild).getTrackScheduler().getQueue();
		}
		return List.of();
	}
	
	public static void stopAll(){
		for(Guild guild : managers.keySet()){
			leave(guild);
		}
	}
	
	public static MusicActionResponse leave(final Guild guild){
		if(managers.containsKey(guild)){
			var manager = managers.get(guild);
			manager.getTrackScheduler().empty();
			manager.getAudioPlayer().stopTrack();
			return OK;
		}
		return NO_MUSIC;
	}
	
	@Override
	public void onTrackSchedulerEmpty(){
		getLogger(getChannel().getGuild()).info("Scheduler is empty");
		var executor = Executors.newScheduledThreadPool(1);
		executor.schedule(() -> {
			getAudioPlayerManager().shutdown();
			getAudioManager().setSendingHandler(null);
			if(getAudioManager().isConnected()){
				getAudioManager().closeAudioConnection();
			}
			managers.remove(channel.getGuild());
			getLogger(getChannel().getGuild()).info("Audio manager shutdown");
		}, 10, TimeUnit.SECONDS);
		executor.shutdown();
	}
	
	public static MusicActionResponse seek(final Guild guild, final long time){
		if(managers.containsKey(guild)){
			final var track = currentTrack(guild);
			if(track.isPresent()){
				if(track.get().isSeekable()){
					track.get().setPosition(time);
					return OK;
				}
				else{
					return IMPOSSIBLE;
				}
			}
		}
		return NO_MUSIC;
	}
	
	public static Optional<AudioTrack> currentTrack(final Guild guild){
		if(managers.containsKey(guild)){
			return Optional.ofNullable(managers.get(guild).getAudioPlayer().getPlayingTrack());
		}
		return Optional.empty();
	}
	
	public static boolean isRequester(final Guild guild, final User user){
		if(managers.containsKey(guild)){
			return currentTrack(guild).map(track -> {
				if(track.getUserData() instanceof TrackUserFields){
					return Objects.equals(user, track.getUserData(TrackUserFields.class).getOrDefault(new RequesterTrackUserField(), null));
				}
				else{
					return false;
				}
			}).orElse(false);
		}
		return false;
	}
	
	public static VoiceChannel currentAudioChannel(final Guild guild){
		if(managers.containsKey(guild)){
			return managers.get(guild).getChannel();
		}
		return null;
	}
	
	public static MusicActionResponse pause(final Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).getAudioPlayer().setPaused(true);
			return OK;
		}
		return NO_MUSIC;
	}
	
	public static MusicActionResponse resume(final Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).getAudioPlayer().setPaused(false);
			return OK;
		}
		return NO_MUSIC;
	}
	
	public static MusicActionResponse skip(final Guild guild){
		if(managers.containsKey(guild)){
			managers.get(guild).skip();
			return OK;
		}
		return NO_MUSIC;
	}
	
	private void skip(){
		trackScheduler.nextTrack();
	}
	
	private VoiceChannel getChannel(){
		return channel;
	}
	
	private AudioManager getAudioManager(){
		return audioManager;
	}
	
	@Override
	public void onTrackEnd(final AudioTrack track){
	}
	
	@Override
	public void onTrackStart(final AudioTrack track){
	}
	
	public AudioPlayer getAudioPlayer(){
		return audioPlayer;
	}
	
	public void addListener(final StatusTrackSchedulerListener listener){
		trackScheduler.addStatusTrackSchedulerListener(listener);
	}
}
