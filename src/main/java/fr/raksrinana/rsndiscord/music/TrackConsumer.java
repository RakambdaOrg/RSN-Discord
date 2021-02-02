package fr.raksrinana.rsndiscord.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public interface TrackConsumer{
	void onPlaylist(@NotNull List<AudioTrack> tracks);
	
	void onTrack(@NotNull AudioTrack track);
	
	void onFailure(@NotNull String message);
}
