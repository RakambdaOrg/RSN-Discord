package fr.rakambda.rsndiscord.spring.audio.load;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.rakambda.rsndiscord.spring.audio.exception.AlreadyInQueueException;
import fr.rakambda.rsndiscord.spring.audio.exception.LoadFailureException;
import fr.rakambda.rsndiscord.spring.audio.exception.NoMatchFoundException;
import fr.rakambda.rsndiscord.spring.audio.scheduler.ITrackScheduler;
import fr.rakambda.rsndiscord.spring.audio.scheduler.TrackUserDataFields;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import java.util.Collection;

@RequiredArgsConstructor
@Slf4j
public class AudioLoadHandler implements AudioLoadResultHandler{
	private final ITrackScheduler scheduler;
	private final Collection<ITrackLoadListener> listeners;
	private final boolean repeat;
	private final Long requesterId;
	private final int skipCount;
	private final int maxTracks;
	
	@Override
	public void trackLoaded(@NonNull AudioTrack track){
		log.info("Added `{}` to the audio queue", track.getIdentifier());
		track.setUserData(TrackUserDataFields.builder()
				.repeat(repeat)
				.requesterId(requesterId)
				.build());
		
		try{
			scheduler.queue(track);
		}
		catch(AlreadyInQueueException e){
			listeners.forEach(l -> l.onLoadFailure(new LoadFailureException(e)));
		}
		
		listeners.forEach(l -> l.onTrackLoaded(track));
	}
	
	@Override
	public void playlistLoaded(@NonNull AudioPlaylist playlist){
		log.info("Added `{}`(size: {}) to the audio queue", playlist.getName(), playlist.getTracks().size());
		playlist.getTracks().stream()
				.skip(skipCount)
				.limit(maxTracks)
				.forEach(this::trackLoaded);
		
		listeners.forEach(l -> l.onPlaylistLoaded(playlist.getTracks()));
	}
	
	@Override
	public void noMatches(){
		log.warn("No match found");
		
		var error = new NoMatchFoundException();
		listeners.forEach(l -> l.onLoadFailure(error));
	}
	
	@Override
	public void loadFailed(@NonNull FriendlyException throwable){
		log.warn("Failed to load track", throwable);
		
		var error = new LoadFailureException(throwable);
		listeners.forEach(l -> l.onLoadFailure(error));
	}
}
