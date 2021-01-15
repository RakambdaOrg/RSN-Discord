package fr.raksrinana.rsndiscord.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.NonNull;

interface IStatusTrackSchedulerListener{
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
