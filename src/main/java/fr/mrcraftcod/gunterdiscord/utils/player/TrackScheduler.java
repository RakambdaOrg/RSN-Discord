package fr.mrcraftcod.gunterdiscord.utils.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import fr.mrcraftcod.gunterdiscord.utils.player.trackfields.ReplayTrackUserField;
import fr.mrcraftcod.gunterdiscord.utils.player.trackfields.TrackUserFields;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.Executors;
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
	private LinkedList<AudioTrack> queue;
	private final Guild guild;
	private final Set<StatusTrackSchedulerListener> listeners;
	
	/**
	 * @param guild  The guild the scheduler is for.
	 * @param player The audio player this scheduler uses
	 */
	TrackScheduler(@Nonnull final Guild guild, @Nonnull final AudioPlayer player){
		this.guild = guild;
		this.player = player;
		this.queue = new LinkedList<>();
		this.listeners = new LinkedHashSet<>();
	}
	
	@Override
	public void onTrackStart(@Nonnull final AudioPlayer player, @Nonnull final AudioTrack track){
		super.onTrackStart(player, track);
		this.listeners.forEach(listener -> listener.onTrackStart(track));
	}
	
	@Override
	public void onTrackEnd(@Nonnull final AudioPlayer player, @Nonnull final AudioTrack track, @Nonnull final AudioTrackEndReason endReason){
		super.onTrackEnd(player, track, endReason);
		if(track.getUserData() instanceof TrackUserFields){
			if(track.getUserData(TrackUserFields.class).get(new ReplayTrackUserField()).orElse(false)){
				getLogger(this.guild).info("Putting back {} into queue: repeat is enabled", track.getInfo().identifier);
				final var clone = track.makeClone();
				clone.setUserData(track.getUserData());
				this.queue(clone);
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
	public void queue(@Nonnull final AudioTrack track){
		if(this.queue.stream().noneMatch(track2 -> Objects.equals(track2.getInfo().identifier, track.getInfo().identifier))){
			this.queue.offer(track);
		}
		if(this.player.startTrack(this.queue.peek(), true)){
			this.queue.poll();
			getLogger(this.guild).info("Playing track {}", track.getInfo().identifier);
		}
	}
	
	/**
	 * Get the guild the scheduler is associated to.
	 *
	 * @return The guild.
	 */
	@Nonnull
	private Guild getGuild(){
		return this.guild;
	}
	
	private void tryStartNext(){
		if(Objects.isNull(this.player.getPlayingTrack()) && !this.nextTrack()){
			getLogger(this.getGuild()).info("Playlist ended, listeners: {}", this.listeners.size());
			final var executor = Executors.newSingleThreadScheduledExecutor();
			executor.schedule(() -> this.listeners.forEach(StatusTrackSchedulerListener::onTrackSchedulerEmpty), 5, TimeUnit.SECONDS);
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
		}
		return this.player.startTrack(next, false);
	}
	
	void skip(){
		this.player.startTrack(null, false);
		this.tryStartNext();
	}
	
	void shuffle(){
		if(!this.queue.isEmpty()){
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
	void addStatusTrackSchedulerListener(@Nonnull final StatusTrackSchedulerListener statusTrackSchedulerListener){
		this.listeners.add(statusTrackSchedulerListener);
	}
	
	void foundNothing(){
		getLogger(this.getGuild()).info("Scheduler nothing found (track: {}, queue: {})", this.player.getPlayingTrack(), this.queue.size());
		if(Objects.isNull(this.player.getPlayingTrack()) && this.queue.isEmpty()){
			this.listeners.forEach(StatusTrackSchedulerListener::onTrackSchedulerEmpty);
		}
	}
	
	@Nonnull
	public List<AudioTrack> getQueue(){
		return this.queue;
	}
}
