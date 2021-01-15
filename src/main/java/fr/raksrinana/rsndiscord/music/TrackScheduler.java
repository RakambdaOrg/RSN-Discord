package fr.raksrinana.rsndiscord.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import fr.raksrinana.rsndiscord.music.trackfields.ReplayTrackDataField;
import fr.raksrinana.rsndiscord.music.trackfields.TrackUserFields;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
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
	TrackScheduler(@NonNull final Guild guild, @NonNull final AudioPlayer player){
		this.guild = guild;
		this.player = player;
		this.queue = new LinkedList<>();
		this.listeners = new LinkedHashSet<>();
	}
	
	@Override
	public void onTrackStart(@NonNull final AudioPlayer player, @NonNull final AudioTrack track){
		super.onTrackStart(player, track);
		this.listeners.forEach(listener -> listener.onTrackStart(track));
	}
	
	@Override
	public void onTrackEnd(@NonNull final AudioPlayer player, @NonNull final AudioTrack track, @NonNull final AudioTrackEndReason endReason){
		super.onTrackEnd(player, track, endReason);
		if(track.getUserData() instanceof TrackUserFields){
			if(track.getUserData(TrackUserFields.class).get(new ReplayTrackDataField()).orElse(false)){
				getLogger(this.guild).info("Putting back {} into queue: repeat is enabled", track.getInfo().identifier);
				this.queue(track.makeClone());
			}
		}
		this.listeners.forEach(listener -> listener.onTrackEnd(track));
		getLogger(this.getGuild()).info("Track ended ({}): {}", endReason.name(), track.getIdentifier());
		if(endReason.mayStartNext){
			this.tryStartNext();
		}
	}
	
	/**
	 * Add the next track to queue or play right away if nothing is in the queue.
	 *
	 * @param track The track to play or add to queue.
	 */
	public void queue(@NonNull final AudioTrack track){
		if(this.queue.stream().noneMatch(track2 -> Objects.equals(track2.getInfo().identifier, track.getInfo().identifier))){
			this.queue.offer(track);
		}
		if(this.player.startTrack(this.queue.peek(), true)){
			this.queue.poll();
			getLogger(this.guild).info("Playing track {}", track.getInfo().identifier);
		}
	}
	
	private void tryStartNext(){
		if(Objects.isNull(this.player.getPlayingTrack()) && !this.nextTrack()){
			getLogger(this.getGuild()).info("Playlist ended, listeners: {}", this.listeners.size());
			final var executor = Executors.newSingleThreadScheduledExecutor();
			executor.schedule(() -> this.listeners.forEach(IStatusTrackSchedulerListener::onTrackSchedulerEmpty), 5, TimeUnit.SECONDS);
		}
	}
	
	/**
	 * Tell if a track is available next.
	 *
	 * @return True if a track is available, false else.
	 */
	private boolean nextTrack(){
		getLogger(this.getGuild()).info("Playing next track");
		final var next = this.queue.poll();
		if(Objects.nonNull(next)){
			getLogger(this.guild).info("Playing track {}", next.getInfo().identifier);
			return this.player.startTrack(next, false);
		}
		return false;
	}
	
	void skip(){
		this.player.startTrack(null, false);
		this.tryStartNext();
	}
	
	void shuffle(){
		if(this.queue.size() > 1){
			final var oldList = new ArrayList<>(this.queue);
			this.queue = new LinkedList<>();
			Collections.shuffle(oldList);
			this.queue.addAll(oldList);
		}
	}
	
	/**
	 * Add a StatusTrackListener.
	 *
	 * @param statusTrackSchedulerListener The listener to add.
	 */
	void addStatusTrackSchedulerListener(@NonNull final IStatusTrackSchedulerListener statusTrackSchedulerListener){
		this.listeners.add(statusTrackSchedulerListener);
	}
	
	void foundNothing(){
		getLogger(this.getGuild()).info("Scheduler nothing found (track: {}, queue: {})", this.player.getPlayingTrack(), this.queue.size());
		if(Objects.isNull(this.player.getPlayingTrack()) && this.queue.isEmpty()){
			this.listeners.forEach(IStatusTrackSchedulerListener::onTrackSchedulerEmpty);
		}
	}
}
