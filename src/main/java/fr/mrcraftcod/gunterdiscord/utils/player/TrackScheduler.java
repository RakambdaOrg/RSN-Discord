package fr.mrcraftcod.gunterdiscord.utils.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
public class TrackScheduler extends AudioEventAdapter
{
	private final AudioPlayer player;
	private final BlockingQueue<AudioTrack> queue;
	private List<StatusTrackSchedulerListener> listeners;
	
	/**
	 * @param player The audio player this scheduler uses
	 */
	public TrackScheduler(AudioPlayer player)
	{
		this.player = player;
		this.queue = new LinkedBlockingQueue<>();
		this.listeners = new ArrayList<>();
	}
	
	/**
	 * Add the next track to queue or play right away if nothing is in the queue.
	 *
	 * @param track The track to play or add to queue.
	 */
	public void queue(AudioTrack track)
	{
		if(!player.startTrack(track, true))
			queue.offer(track);
	}
	
	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
	{
		Log.info("Next track");
		if(endReason.mayStartNext)
			if(!nextTrack())
			{
				Log.info("Playlist ended - " + listeners.size());
				listeners.forEach(StatusTrackSchedulerListener::onTrackSchedulerEmpty);
			}
	}
	
	/**
	 * Start the next track, stopping the current one if it is playing.
	 */
	public boolean nextTrack()
	{
		return player.startTrack(queue.poll(), false);
	}
	
	public void addStatusTrackSchedulerListener(StatusTrackSchedulerListener statusTrackSchedulerListener)
	{
		listeners.add(statusTrackSchedulerListener);
	}
	
	public void foundNothing()
	{
		Log.info("Scheduler nothing found (track: " + player.getPlayingTrack() + ", queue:" + queue.size() + ")");
		if(player.getPlayingTrack() == null && queue.isEmpty())
			listeners.forEach(StatusTrackSchedulerListener::onTrackSchedulerEmpty);
	}
}
