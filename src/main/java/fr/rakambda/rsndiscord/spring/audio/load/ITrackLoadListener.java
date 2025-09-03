package fr.rakambda.rsndiscord.spring.audio.load;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.rakambda.rsndiscord.spring.audio.exception.TrackLoadException;
import org.jspecify.annotations.NonNull;
import java.util.Collection;

public interface ITrackLoadListener{
	void onTrackLoaded(@NonNull AudioTrack track);
	
	void onPlaylistLoaded(@NonNull Collection<AudioTrack> tracks);
	
	void onLoadFailure(@NonNull TrackLoadException throwable);
}
