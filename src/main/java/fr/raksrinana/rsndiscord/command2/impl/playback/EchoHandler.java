package fr.raksrinana.rsndiscord.command2.impl.playback;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.audio.UserAudio;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EchoHandler implements AudioSendHandler, AudioReceiveHandler{
	private final Queue<byte[]> queue = new ConcurrentLinkedQueue<>();
	
	@Override
	public boolean canReceiveUser(){
		return queue.size() < 10;
	}
	
	@Override
	public void handleUserAudio(UserAudio userAudio){
		byte[] data = userAudio.getAudioData(1.0f);
		queue.add(data);
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
