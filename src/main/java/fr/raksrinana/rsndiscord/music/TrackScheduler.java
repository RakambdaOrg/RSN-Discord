package fr.raksrinana.rsndiscord.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import fr.raksrinana.rsndiscord.music.trackfields.ReplayTrackDataField;
import fr.raksrinana.rsndiscord.music.trackfields.TrackUserFields;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static fr.raksrinana.rsndiscord.log.Log.getLogger;

class TrackScheduler extends AudioEventAdapter{
	private final AudioPlayer player;
	@Getter
	private final Guild guild;
	private final Set<IStatusTrackSchedulerListener> listeners;
	@Getter
	private LinkedList<AudioTrack> queue;
	
	/**
	 * @param guild  The guild the scheduler is for.
	 * @param player The audio player this scheduler uses.
	 */
	TrackScheduler(@NotNull Guild guild, @NotNull AudioPlayer player){
		this.guild = guild;
		this.player = player;
		queue = new LinkedList<>();
		listeners = new LinkedHashSet<>();
	}
	
	@Override
	public void onTrackStart(@NotNull AudioPlayer player, @NotNull AudioTrack track){
		super.onTrackStart(player, track);
		listeners.forEach(listener -> listener.onTrackStart(track));
	}
	
	@Override
	public void onTrackEnd(@NotNull AudioPlayer player, @NotNull AudioTrack track, @NotNull AudioTrackEndReason endReason){
		super.onTrackEnd(player, track, endReason);
		if(track.getUserData() instanceof TrackUserFields){
			if(track.getUserData(TrackUserFields.class).get(new ReplayTrackDataField()).orElse(false)){
				getLogger(guild).info("Putting back {} into queue: repeat is enabled", track.getInfo().identifier);
				queue(track.makeClone());
			}
		}
		listeners.forEach(listener -> listener.onTrackEnd(track));
		getLogger(getGuild()).info("Track ended ({}): {}", endReason.name(), track.getIdentifier());
		if(endReason.mayStartNext){
			tryStartNext();
		}
	}
	
	/**
	 * Add the next track to queue or play right away if nothing is in the queue.
	 *
	 * @param track The track to play or add to queue.
	 */
	public void queue(@NotNull AudioTrack track){
		if(queue.stream().noneMatch(track2 -> Objects.equals(track2.getInfo().identifier, track.getInfo().identifier))){
			queue.offer(track);
		}
		if(player.startTrack(queue.peek(), true)){
			queue.poll();
			getLogger(guild).info("Playing track {}", track.getInfo().identifier);
		}
	}
	
	private void tryStartNext(){
		if(Objects.isNull(player.getPlayingTrack()) && !nextTrack()){
			getLogger(getGuild()).info("Playlist ended, listeners: {}", listeners.size());
			var executor = Executors.newSingleThreadScheduledExecutor();
			executor.schedule(() -> listeners.forEach(IStatusTrackSchedulerListener::onTrackSchedulerEmpty), 5, TimeUnit.SECONDS);
		}
	}
	
	/**
	 * Tell if a track is available next.
	 *
	 * @return True if a track is available, false else.
	 */
	private boolean nextTrack(){
		getLogger(getGuild()).info("Playing next track");
		var next = queue.poll();
		if(Objects.nonNull(next)){
			getLogger(guild).info("Playing track {}", next.getInfo().identifier);
			return player.startTrack(next, false);
		}
		return false;
	}
	
	void skip(){
		player.startTrack(null, false);
		tryStartNext();
	}
	
	void shuffle(){
		if(queue.size() > 1){
			var oldList = new ArrayList<>(queue);
			queue = new LinkedList<>();
			Collections.shuffle(oldList);
			queue.addAll(oldList);
		}
	}
	
	/**
	 * Add a StatusTrackListener.
	 *
	 * @param statusTrackSchedulerListener The listener to add.
	 */
	void addStatusTrackSchedulerListener(@NotNull IStatusTrackSchedulerListener statusTrackSchedulerListener){
		listeners.add(statusTrackSchedulerListener);
	}
	
	void foundNothing(){
		getLogger(getGuild()).info("Scheduler nothing found (track: {}, queue: {})", player.getPlayingTrack(), queue.size());
		if(Objects.isNull(player.getPlayingTrack()) && queue.isEmpty()){
			listeners.forEach(IStatusTrackSchedulerListener::onTrackSchedulerEmpty);
		}
	}
}
