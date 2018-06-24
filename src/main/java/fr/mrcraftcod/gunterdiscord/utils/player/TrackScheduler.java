package fr.mrcraftcod.gunterdiscord.utils.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import fr.mrcraftcod.gunterdiscord.utils.Log;
import java.util.LinkedHashSet;
import java.util.Set;
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
	private Set<StatusTrackSchedulerListener> listeners;
	
	/**
	 * @param player The audio player this scheduler uses
	 */
	public TrackScheduler(AudioPlayer player)
	{
		this.player = player;
		this.queue = new LinkedBlockingQueue<>();
		this.listeners = new LinkedHashSet<>();
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
	public void onTrackStart(AudioPlayer player, AudioTrack track)
	{
		super.onTrackStart(player, track);
		listeners.forEach(l -> l.onTrackStart(track));
	}
	
	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason)
	{
		super.onTrackEnd(player, track, endReason);
		listeners.forEach(l -> l.onTrackEnd(track));
		Log.info("Next track");
		if(endReason.mayStartNext)
			if(!nextTrack())
			{
				Log.info("Playlist ended - " + listeners.size());
				listeners.forEach(StatusTrackSchedulerListener::onTrackSchedulerEmpty);
			}
	}
	
	public boolean nextTrack()
	{
		Log.info("Playing next track");
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
