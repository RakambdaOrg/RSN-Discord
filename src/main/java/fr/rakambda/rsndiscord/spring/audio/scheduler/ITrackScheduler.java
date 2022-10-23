package fr.rakambda.rsndiscord.spring.audio.scheduler;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.rakambda.rsndiscord.spring.audio.exception.AlreadyInQueueException;
import fr.rakambda.rsndiscord.spring.audio.exception.NoTrackPlayingException;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Optional;

public interface ITrackScheduler{
	void queue(@NotNull AudioTrack track) throws AlreadyInQueueException;
	
	@NotNull
	List<AudioTrack> getQueue();
	
	@NotNull
	Optional<AudioTrack> getCurrentTrack();
	
	boolean pause(boolean paused);
	
	void skip() throws NoTrackPlayingException;
	
	void seek(long time) throws NoTrackPlayingException;
	
	void setVolume(int volume);
}
