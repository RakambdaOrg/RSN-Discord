package fr.rakambda.rsndiscord.spring.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@Slf4j
public class AudioService implements ITrackSchedulerStatusListener, ITrackLoadListener{
	private final Guild guild;
	private final AudioPlayerManager audioPlayerManager;
	private final AudioPlayer audioPlayer;
	@Getter
	private final ITrackScheduler trackScheduler;
	
	public AudioService(@NotNull Guild guild, int volume, @Nullable String refreshToken){
		this.guild = guild;
		
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
		
		audioPlayerManager = new DefaultAudioPlayerManager();
		audioPlayerManager.registerSourceManager(youtubeAudioSourceManager);
		audioPlayer = audioPlayerManager.createPlayer();
		
		var trackScheduler = new TrackScheduler(audioPlayer);
		trackScheduler.addListener(this);
		this.trackScheduler = trackScheduler;
		
		audioPlayer.setVolume(volume);
		audioPlayer.addListener(trackScheduler);
		
		AudioSourceManagers.registerRemoteSources(audioPlayerManager);
	}
	
	@Override
	public void onTrackSchedulerEmpty(){
		leave();
	}
	
	@Override
	public void onTrackEnd(@NotNull AudioTrack track){
	}
	
	@Override
	public void onTrackStart(@NotNull AudioTrack track){
	}
	
	@Override
	public void onTrackLoaded(@NotNull AudioTrack track){
	}
	
	@Override
	public void onPlaylistLoaded(@NotNull Collection<AudioTrack> tracks){
	}
	
	@Override
	public void onLoadFailure(@NotNull TrackLoadException throwable){
		if(trackScheduler.getCurrentTrack().isEmpty() && trackScheduler.getQueue().isEmpty()){
			onTrackSchedulerEmpty();
		}
	}
	
	public boolean isConnected(){
		return guild.getAudioManager().isConnected();
	}
	
	public boolean join(@NotNull AudioChannelUnion audioChannel){
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
	
	public void play(@NotNull User requester, @NotNull String identifier, boolean repeat, int skipCount, int maxTracks, @Nullable ITrackLoadListener listener) throws LoadFailureException{
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
