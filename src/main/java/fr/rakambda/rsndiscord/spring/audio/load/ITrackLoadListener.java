package fr.rakambda.rsndiscord.spring.audio.load;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import fr.rakambda.rsndiscord.spring.audio.exception.TrackLoadException;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;

public interface ITrackLoadListener{
	void onTrackLoaded(@NotNull AudioTrack track);
	
	void onPlaylistLoaded(@NotNull Collection<AudioTrack> tracks);
	
	void onLoadFailure(@NotNull TrackLoadException throwable);
}
