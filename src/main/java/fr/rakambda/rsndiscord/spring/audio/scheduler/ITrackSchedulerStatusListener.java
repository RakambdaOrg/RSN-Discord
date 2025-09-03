package fr.rakambda.rsndiscord.spring.audio.scheduler;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.jspecify.annotations.NonNull;

public interface ITrackSchedulerStatusListener{
	/**
	 * Called when the queue is completed.
	 */
	void onTrackSchedulerEmpty();
	
	/**
	 * Called when a track ends.
	 *
	 * @param track The track that ended.
	 */
	void onTrackEnd(@NonNull AudioTrack track);
	
	/**
	 * Called when a track starts.
	 *
	 * @param track The track that started.
	 */
	void onTrackStart(@NonNull AudioTrack track);
}
