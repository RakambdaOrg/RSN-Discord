package fr.rakambda.rsndiscord.spring.audio.scheduler;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import fr.rakambda.rsndiscord.spring.audio.exception.AlreadyInQueueException;
import fr.rakambda.rsndiscord.spring.audio.exception.NoTrackPlayingException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class TrackScheduler extends AudioEventAdapter implements ITrackScheduler{
	private final AudioPlayer player;
	private final Set<ITrackSchedulerStatusListener> listeners;
	private final LinkedList<AudioTrack> queue;
	
	public TrackScheduler(@NotNull AudioPlayer player){
		this.player = player;
		
		queue = new LinkedList<>();
		listeners = new LinkedHashSet<>();
	}
	
	@Override
	public void onTrackStart(@NotNull AudioPlayer player, @NotNull AudioTrack track){
		super.onTrackStart(player, track);
		log.info("Audio track started {}", track.getIdentifier());
		
		listeners.forEach(listener -> listener.onTrackStart(track));
	}
	
	@Override
	public void onTrackEnd(@NotNull AudioPlayer player, @NotNull AudioTrack track, @NotNull AudioTrackEndReason endReason){
		super.onTrackEnd(player, track, endReason);
		log.info("Track ended ({}): {}", endReason.name(), track.getIdentifier());
		
		var userFields = track.getUserData(TrackUserDataFields.class);
		if(Objects.nonNull(userFields)){
			if(userFields.isRepeat()){
				log.info("Putting back {} into queue: repeat is enabled", track.getIdentifier());
				internalQueue(track.makeClone());
			}
		}
		
		listeners.forEach(listener -> listener.onTrackEnd(track));
		if(endReason.mayStartNext){
			tryStartNext();
		}
	}
	
	@Override
	public void skip() throws NoTrackPlayingException{
		log.info("Skipping current track");
		var track = getCurrentTrack().orElseThrow(NoTrackPlayingException::new);
		
		var userFields = track.getUserData(TrackUserDataFields.class);
		if(Objects.nonNull(userFields)){
			userFields.setRepeat(false);
		}
		
		player.startTrack(null, false);
		tryStartNext();
	}
	
	@Override
	@NotNull
	public List<AudioTrack> getQueue(){
		return Collections.unmodifiableList(queue);
	}
	
	@NotNull
	public Optional<AudioTrack> getCurrentTrack(){
		return Optional.ofNullable(player.getPlayingTrack());
	}
	
	@Override
	public boolean pause(boolean paused){
		if(paused && getCurrentTrack().isEmpty()){
			log.info("Skipped pausing track, not playing");
			return false;
		}
		
		log.info("Pausing current track");
		player.setPaused(paused);
		return true;
	}
	
	@Override
	public void seek(long time) throws NoTrackPlayingException{
		log.info("Seeking to {}", time);
		var track = getCurrentTrack().orElseThrow(NoTrackPlayingException::new);
		
		if(!track.isSeekable()){
			throw new IllegalStateException("Current rack doesn't support seeking feature");
		}
		
		track.setPosition(time);
	}
	
	@Override
	public void setVolume(int volume){
		log.info("Setting volume to {}", volume);
		player.setVolume(volume);
	}
	
	public void queue(@NotNull AudioTrack track) throws AlreadyInQueueException{
		log.info("Queuing new track {}", track.getIdentifier());
		
		var alreadyExists = queue.stream().anyMatch(track2 -> Objects.equals(track2.getIdentifier(), track.getIdentifier()));
		if(alreadyExists){
			throw new AlreadyInQueueException();
		}
		
		internalQueue(track);
	}
	
	private void internalQueue(@NotNull AudioTrack track){
		queue.offer(track);
		tryStartNext();
	}
	
	private void tryStartNext(){
		if(Objects.isNull(player.getPlayingTrack()) && !nextTrack()){
			log.info("Playlist ended");
			listeners.forEach(ITrackSchedulerStatusListener::onTrackSchedulerEmpty);
		}
	}
	
	private boolean nextTrack(){
		log.info("Playing next track");
		var next = queue.poll();
		if(Objects.nonNull(next)){
			log.info("Playing track {}", next.getInfo().identifier);
			return player.startTrack(next, false);
		}
		return false;
	}
	
	public void addListener(@NotNull ITrackSchedulerStatusListener listener){
		listeners.add(listener);
	}
}
