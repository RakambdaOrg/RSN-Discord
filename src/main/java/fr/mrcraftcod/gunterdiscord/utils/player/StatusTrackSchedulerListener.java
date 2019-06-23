package fr.mrcraftcod.gunterdiscord.utils.player;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import javax.annotation.Nonnull;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
public interface StatusTrackSchedulerListener{
	/**
	 * Called when the queue is completed.
	 */
	void onTrackSchedulerEmpty();
	
	/**
	 * Called when a track ends.
	 *
	 * @param track The track that ended.
	 */
	void onTrackEnd(@Nonnull AudioTrack track);
	
	/**
	 * Called when a track starts.
	 *
	 * @param track The track that started.
	 */
	void onTrackStart(@Nonnull AudioTrack track);
}
