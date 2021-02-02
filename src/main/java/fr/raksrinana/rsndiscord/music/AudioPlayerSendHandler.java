package fr.raksrinana.rsndiscord.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
	AudioPlayerSendHandler(@NotNull AudioPlayer audioPlayer){
		this.audioPlayer = audioPlayer;
		audioPlayer.setVolume(DEFAULT_VOLUME);
	}
	
	@Override
	public boolean canProvide(){
		if(isNull(lastFrame)){
			lastFrame = audioPlayer.provide();
		}
		return nonNull(lastFrame);
	}
	
	@Override
	@Nullable
	public ByteBuffer provide20MsAudio(){
		if(isNull(lastFrame)){
			lastFrame = audioPlayer.provide();
		}
		if(nonNull(lastFrame)){
			var data = lastFrame.getData();
			lastFrame = null;
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