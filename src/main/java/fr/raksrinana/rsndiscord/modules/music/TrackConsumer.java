package fr.raksrinana.rsndiscord.modules.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;

public interface TrackConsumer{
	void onPlaylist(List<AudioTrack> tracks);
	
	void onTrack(AudioTrack track);
	
	void onFailure(String message);
}
