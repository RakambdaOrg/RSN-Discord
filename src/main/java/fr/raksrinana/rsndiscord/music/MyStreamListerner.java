package fr.raksrinana.rsndiscord.music;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import javax.sound.sampled.AudioInputStream;
import java.io.ByteArrayInputStream;
import java.util.Arrays;

public class MyStreamListerner implements AudioReceiveHandler{
	int currentFrame = 0;
	int totalCaptureSeconds = 10;
	int totalCaptureFrames = (totalCaptureSeconds * 1000) / 20;  //20 milliseconds per frame
	byte[] audioBytes = new byte[0];
	
	@Override
	public boolean canReceiveCombined(){
		return true;
	}
	
	//Called once per 20 milliseconds
	@Override
	public void handleCombinedAudio(CombinedAudio combinedAudio){
		this.currentFrame++;
		this.audioBytes = this.concatByteArrays(this.audioBytes, combinedAudio.getAudioData(1.0));
		
		//If we don't have enough data captured yet, exit the method to wait for more.
		if(this.currentFrame < this.totalCaptureFrames){
			return;
		}
		
		ByteArrayInputStream rawInputStream = new ByteArrayInputStream(this.audioBytes);
		AudioInputStream audioInputStream = new AudioInputStream(
				rawInputStream,
				AudioReceiveHandler.OUTPUT_FORMAT,
				this.audioBytes.length
		);
		
		//Reset to capture another 10 seconds.
		this.currentFrame = 0;
		this.audioBytes = new byte[0];
		
		//Do something with the audioInputStream
		// myCoolLibrary.processAudio(audioInputStream);
	}
	
	private byte[] concatByteArrays(byte[] first, byte[] second){
		byte[] both = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, both, first.length, second.length);
		
		return both;
	}
}
