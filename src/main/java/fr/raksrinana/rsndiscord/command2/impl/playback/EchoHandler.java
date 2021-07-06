package fr.raksrinana.rsndiscord.command2.impl.playback;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class EchoHandler implements AudioSendHandler, AudioReceiveHandler{
	public static final int MAX_QUEUE_SIZE = 100;
	private final Queue<byte[]> queue = new ConcurrentLinkedQueue<>();
	
	@Override
	public boolean canReceiveCombined(){
		return queue.size() < MAX_QUEUE_SIZE;
	}
	
	@Override
	public void handleCombinedAudio(@NotNull CombinedAudio combinedAudio){
		if (combinedAudio.getUsers().isEmpty())
			return;
		
		byte[] data = combinedAudio.getAudioData(1.0f);
		queue.add(data);
	}
	
	@Override
	public boolean includeUserInCombinedAudio(@NotNull User user){
		return true;
	}
	
	@Override
	public boolean canProvide(){
		return !queue.isEmpty();
	}
	
	@Override
	public ByteBuffer provide20MsAudio(){
		byte[] data = queue.poll();
		return data == null ? null : ByteBuffer.wrap(data);
	}
	
	@Override
	public boolean isOpus(){
		return false;
	}
}
