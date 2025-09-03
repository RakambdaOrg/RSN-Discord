package fr.rakambda.rsndiscord.spring.audio.scheduler;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.rakambda.rsndiscord.spring.audio.exception.AlreadyInQueueException;
import fr.rakambda.rsndiscord.spring.audio.exception.NoTrackPlayingException;
import org.jspecify.annotations.NonNull;
import java.util.List;
import java.util.Optional;

public interface ITrackScheduler{
	void queue(@NonNull AudioTrack track) throws AlreadyInQueueException;
	
	@NonNull
	List<AudioTrack> getQueue();
	
	@NonNull
	Optional<AudioTrack> getCurrentTrack();
	
	boolean pause(boolean paused);
	
	void skip() throws NoTrackPlayingException;
	
	void seek(long time) throws NoTrackPlayingException;
	
	void setVolume(int volume);
}
