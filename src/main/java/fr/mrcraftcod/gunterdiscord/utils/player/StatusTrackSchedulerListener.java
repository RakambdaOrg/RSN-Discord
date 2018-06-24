package fr.mrcraftcod.gunterdiscord.utils.player;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
public interface StatusTrackSchedulerListener
{
	void onTrackSchedulerEmpty();
	
	void onTrackEnd(AudioTrack track);
	
	void onTrackStart(AudioTrack track);
}
