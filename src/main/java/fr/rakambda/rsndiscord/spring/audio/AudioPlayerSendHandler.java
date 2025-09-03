package fr.rakambda.rsndiscord.spring.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.jspecify.annotations.Nullable;
import java.nio.ByteBuffer;
import static java.nio.ByteBuffer.wrap;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
public class AudioPlayerSendHandler implements AudioSendHandler{
	private final AudioPlayer audioPlayer;
	private AudioFrame lastFrame;
	
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