package fr.raksrinana.rsndiscord.modules.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import lombok.NonNull;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import java.nio.ByteBuffer;
import static java.nio.ByteBuffer.wrap;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

class AudioPlayerSendHandler implements AudioSendHandler{
	private static final int DEFAULT_VOLUME = 75;
	private final AudioPlayer audioPlayer;
	private AudioFrame lastFrame;
	
	/**
	 * @param audioPlayer Audio player to wrap.
	 */
	AudioPlayerSendHandler(@NonNull final AudioPlayer audioPlayer){
		this.audioPlayer = audioPlayer;
		audioPlayer.setVolume(DEFAULT_VOLUME);
	}
	
	@Override
	public boolean canProvide(){
		if(isNull(this.lastFrame)){
			this.lastFrame = this.audioPlayer.provide();
		}
		return nonNull(this.lastFrame);
	}
	
	@Override
	public ByteBuffer provide20MsAudio(){
		if(isNull(this.lastFrame)){
			this.lastFrame = this.audioPlayer.provide();
		}
		if(nonNull(this.lastFrame)){
			final var data = this.lastFrame.getData();
			this.lastFrame = null;
			if(nonNull(data)){
				return wrap(data);
			}
		}
		return null;
	}
	
	@Override
	public boolean isOpus(){
		return true;
	}
}