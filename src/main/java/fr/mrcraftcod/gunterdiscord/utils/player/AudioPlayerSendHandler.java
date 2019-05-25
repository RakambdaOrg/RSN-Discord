package fr.mrcraftcod.gunterdiscord.utils.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-06-16
 */
class AudioPlayerSendHandler implements AudioSendHandler{
	private final AudioPlayer audioPlayer;
	private AudioFrame lastFrame;
	
	/**
	 * @param audioPlayer Audio player to wrap.
	 */
	AudioPlayerSendHandler(final AudioPlayer audioPlayer){
		this.audioPlayer = audioPlayer;
		audioPlayer.setVolume(75);
	}
	
	@Override
	public boolean canProvide(){
		if(Objects.isNull(this.lastFrame)){
			this.lastFrame = this.audioPlayer.provide();
		}
		return Objects.nonNull(this.lastFrame);
	}
	
	@Override
	public ByteBuffer provide20MsAudio(){
		if(Objects.isNull(this.lastFrame)){
			this.lastFrame = this.audioPlayer.provide();
		}
		if(Objects.nonNull(this.lastFrame)){
			final var data = this.lastFrame.getData();
			this.lastFrame = null;
			if(Objects.nonNull(data)){
				return ByteBuffer.wrap(data);
			}
		}
		return null;
	}
	
	@Override
	public boolean isOpus(){
		return true;
	}
}