package fr.mrcraftcod.gunterdiscord.utils.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import fr.mrcraftcod.gunterdiscord.utils.log.Log;
import fr.mrcraftcod.gunterdiscord.utils.player.trackfields.ReplayTrackUserField;
import fr.mrcraftcod.gunterdiscord.utils.player.trackfields.TrackUserFields;
import net.dv8tion.jda.core.entities.Guild;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import static fr.mrcraftcod.gunterdiscord.utils.log.Log.getLogger;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
class TrackScheduler extends AudioEventAdapter{
	private final AudioPlayer player;
	private BlockingQueue<AudioTrack> queue;
	private final Guild guild;
	private final Set<StatusTrackSchedulerListener> listeners;
	
	/**
	 * @param guild  The guild the scheduler is for.
	 * @param player The audio player this scheduler uses
	 */
	TrackScheduler(final Guild guild, final AudioPlayer player){
		this.guild = guild;
		this.player = player;
		this.queue = new LinkedBlockingQueue<>();
		this.listeners = new LinkedHashSet<>();
	}
	
	/**
	 * Add the next track to queue or play right away if nothing is in the queue.
	 *
	 * @param track The track to play or add to queue.
	 */
	public void queue(final AudioTrack track){
		if(queue.stream().noneMatch(track2 -> Objects.equals(track2.getInfo().identifier, track.getInfo().identifier))){
			queue.offer(track);
		}
		if(player.startTrack(queue.peek(), true)){
			queue.poll();
			getLogger(guild).info("Playing track {}", track.getInfo().identifier);
		}
	}
	
	@Override
	public void onTrackStart(final AudioPlayer player, final AudioTrack track){
		super.onTrackStart(player, track);
		listeners.forEach(l -> l.onTrackStart(track));
	}
	
	@Override
	public void onTrackEnd(final AudioPlayer player, final AudioTrack track, final AudioTrackEndReason endReason){
		super.onTrackEnd(player, track, endReason);
		if(track.getUserData() instanceof TrackUserFields){
			if(track.getUserData(TrackUserFields.class).getOrDefault(new ReplayTrackUserField(), false)){
				getLogger(guild).info("Putting back {} into queue: repeat is enabled", track.getInfo().identifier);
				final var clone = track.makeClone();
				clone.setUserData(track.getUserData());
				queue(clone);
			}
		}
		listeners.forEach(l -> l.onTrackEnd(track));
		getLogger(getGuild()).info("Track ended ({}): {}", endReason.name(), track.getIdentifier());
		if(endReason.mayStartNext){
			tryStartNext();
		}
	}
	
	private void tryStartNext(){
		if(Objects.isNull(this.player.getPlayingTrack()) && !nextTrack()){
			getLogger(getGuild()).info("Playlist ended, listeners: {}", listeners.size());
			final var executor = Executors.newSingleThreadScheduledExecutor();
			executor.schedule(() -> listeners.forEach(StatusTrackSchedulerListener::onTrackSchedulerEmpty), 5, TimeUnit.SECONDS);
		}
	}
	
	public void skip(){
		this.player.startTrack(null, false);
		tryStartNext();
	}
	
	public void shuffle(){
		if(this.queue.size() > 1){
			final var oldList = new ArrayList<>(this.queue);
			this.queue = new LinkedBlockingQueue<>();
			Collections.shuffle(oldList);
			this.queue.addAll(oldList);
		}
	}
	
	/**
	 * Get the guild the scheduler is associated to.
	 *
	 * @return The guild.
	 */
	private Guild getGuild(){
		return guild;
	}
	
	/**
	 * Tell if a track is available next.
	 *
	 * @return True if a track is available, false else.
	 */
	private boolean nextTrack(){
		getLogger(getGuild()).info("Playing next track");
		final var next = queue.poll();
		if(Objects.nonNull(next)){
			getLogger(guild).info("Playing track {}", next.getInfo().identifier);
		}
		return player.startTrack(next, false);
	}
	
	public void empty(){
		Log.getLogger(getGuild()).info("Cleared track scheduler");
		queue.clear();
	}
	
	/**
	 * Add a StatusTrackListener.
	 *
	 * @param statusTrackSchedulerListener The listener to add.
	 */
	void addStatusTrackSchedulerListener(final StatusTrackSchedulerListener statusTrackSchedulerListener){
		listeners.add(statusTrackSchedulerListener);
	}
	
	void foundNothing(){
		getLogger(getGuild()).info("Scheduler nothing found (track: {}, queue: {})", player.getPlayingTrack(), queue.size());
		if(player.getPlayingTrack() == null && queue.isEmpty()){
			listeners.forEach(StatusTrackSchedulerListener::onTrackSchedulerEmpty);
		}
	}
	
	public Collection<AudioTrack> getQueue(){
		return queue;
	}
}
