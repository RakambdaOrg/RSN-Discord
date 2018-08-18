package fr.mrcraftcod.gunterdiscord.listeners.musicparty;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-23
 */
class MusicPartyMusic{
	private final AudioTrack track;
	
	/**
	 * Constructor.
	 *
	 * @param track The track.
	 */
	MusicPartyMusic(final AudioTrack track){
		this.track = track;
	}
	
	@Override
	public String toString(){
		return String.format("%s (author: %s, length: %d, id: %s)", getTitle(), getTrack().getInfo().author, getTrack().getInfo().length, getTrack().getInfo().identifier);
	}
	
	/**
	 * Get the title of the track.
	 *
	 * @return The title.
	 */
	public String getTitle(){
		return getTrack().getInfo().title;
	}
	
	/**
	 * Get the track.
	 *
	 * @return The track.
	 */
	public AudioTrack getTrack(){
		return track;
	}
}
