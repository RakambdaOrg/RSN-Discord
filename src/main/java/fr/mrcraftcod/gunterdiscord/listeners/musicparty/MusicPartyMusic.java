package fr.mrcraftcod.gunterdiscord.listeners.musicparty;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-23
 */
public class MusicPartyMusic
{
	private final AudioTrack track;
	
	public MusicPartyMusic(AudioTrack track)
	{
		this.track = track;
	}
	
	public String getTitle()
	{
		return getTrack().getInfo().title;
	}
	
	public AudioTrack getTrack()
	{
		return track;
	}
	
	@Override
	public String toString()
	{
		return getTitle();
	}
}
