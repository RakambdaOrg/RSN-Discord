package fr.rakambda.rsndiscord.spring.audio;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import com.github.topi314.lavasrc.mirror.DefaultMirroringAudioTrackResolver;
import com.github.topi314.lavasrc.spotify.SpotifySourceManager;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.AndroidVrWithThumbnail;
import dev.lavalink.youtube.clients.MWebWithThumbnail;
import dev.lavalink.youtube.clients.MusicWithThumbnail;
import dev.lavalink.youtube.clients.TvHtml5EmbeddedWithThumbnail;
import dev.lavalink.youtube.clients.WebWithThumbnail;
import fr.rakambda.rsndiscord.spring.audio.exception.LoadFailureException;
import fr.rakambda.rsndiscord.spring.audio.exception.TrackLoadException;
import fr.rakambda.rsndiscord.spring.audio.load.AudioLoadHandler;
import fr.rakambda.rsndiscord.spring.audio.load.ITrackLoadListener;
import fr.rakambda.rsndiscord.spring.audio.scheduler.ITrackScheduler;
import fr.rakambda.rsndiscord.spring.audio.scheduler.ITrackSchedulerStatusListener;
import fr.rakambda.rsndiscord.spring.audio.scheduler.TrackScheduler;
import fr.rakambda.rsndiscord.spring.settings.MusicSettings;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@Slf4j
public class AudioService implements ITrackSchedulerStatusListener, ITrackLoadListener{
	private final Guild guild;
	private final AudioPlayerManager audioPlayerManager;
	private final AudioPlayer audioPlayer;
	@Getter
	private final ITrackScheduler trackScheduler;
	
	public AudioService(@NonNull Guild guild, int volume, @NonNull MusicSettings musicSettings){
		this.guild = guild;
		
		audioPlayerManager = new DefaultAudioPlayerManager();
		audioPlayerManager.registerSourceManager(createYoutubeSourceManager(musicSettings.getYoutubeRefreshToken()));
		audioPlayerManager.registerSourceManager(createSpotifySourceManager(musicSettings.getSpotifyClientId(), musicSettings.getSpotifyClientSecret(), musicSettings.getSpotifyCountryCode()));
		audioPlayer = audioPlayerManager.createPlayer();
		
		var trackScheduler = new TrackScheduler(audioPlayer);
		trackScheduler.addListener(this);
		this.trackScheduler = trackScheduler;
		
		audioPlayer.setVolume(volume);
		audioPlayer.addListener(trackScheduler);
		
		AudioSourceManagers.registerRemoteSources(audioPlayerManager);
	}
	
	@NonNull
	private AudioSourceManager createSpotifySourceManager(@NonNull String clientId, @NonNull String clientSecret, @Nullable String countryCode){
		return new SpotifySourceManager(clientId, clientSecret, countryCode, audioPlayerManager, new DefaultMirroringAudioTrackResolver(null));
	}
	
	@NonNull
	private AudioSourceManager createYoutubeSourceManager(@Nullable String refreshToken){
		var youtubeAudioSourceManager = new YoutubeAudioSourceManager(true,
				new MusicWithThumbnail(),
				new WebWithThumbnail(),
				new AndroidVrWithThumbnail(),
				new TvHtml5EmbeddedWithThumbnail(),
				new MWebWithThumbnail()
		);
		if(Objects.nonNull(refreshToken)){
			youtubeAudioSourceManager.useOauth2(refreshToken, true);
		}
		else{
			youtubeAudioSourceManager.useOauth2(null, false);
		}
		return youtubeAudioSourceManager;
	}
	
	@Override
	public void onTrackSchedulerEmpty(){
		leave();
	}
	
	@Override
	public void onTrackEnd(@NonNull AudioTrack track){
	}
	
	@Override
	public void onTrackStart(@NonNull AudioTrack track){
	}
	
	@Override
	public void onTrackLoaded(@NonNull AudioTrack track){
	}
	
	@Override
	public void onPlaylistLoaded(@NonNull Collection<AudioTrack> tracks){
	}
	
	@Override
	public void onLoadFailure(@NonNull TrackLoadException throwable){
		if(trackScheduler.getCurrentTrack().isEmpty() && trackScheduler.getQueue().isEmpty()){
			onTrackSchedulerEmpty();
		}
	}
	
	public boolean isConnected(){
		return guild.getAudioManager().isConnected();
	}
	
	public boolean join(@NonNull AudioChannelUnion audioChannel){
		var audioManager = guild.getAudioManager();
		if(isConnected()){
			var connectedChannel = audioManager.getConnectedChannel();
			if(audioChannel.equals(connectedChannel)){
				return true;
			}
			log.warn("Wanted to join {} but already in {}", audioChannel, connectedChannel);
			return false;
		}
		
		audioManager.openAudioConnection(audioChannel);
		audioManager.setSendingHandler(new AudioPlayerSendHandler(audioPlayer));
		
		log.info("Audio service joined {}", audioChannel);
		return true;
	}
	
	public void leave(){
		if(!isConnected()){
			return;
		}
		
		audioPlayer.stopTrack();
		
		var audioManager = guild.getAudioManager();
		audioManager.closeAudioConnection();
		audioManager.setSendingHandler(null);
		
		log.info("Audio service shutdown");
	}
	
	public void play(@NonNull User requester, @NonNull String identifier, boolean repeat, int skipCount, int maxTracks, @Nullable ITrackLoadListener listener) throws LoadFailureException{
		try{
			var listeners = Objects.isNull(listener) ? List.<ITrackLoadListener> of(this) : List.of(this, listener);
			var audioLoadResultHandler = new AudioLoadHandler(trackScheduler, listeners, repeat, requester.getIdLong(), skipCount, maxTracks);
			audioPlayerManager.loadItem(identifier, audioLoadResultHandler).get();
		}
		catch(InterruptedException | ExecutionException e){
			throw new LoadFailureException(e);
		}
	}
}
