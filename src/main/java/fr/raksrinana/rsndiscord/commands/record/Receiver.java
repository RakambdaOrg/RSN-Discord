package fr.raksrinana.rsndiscord.commands.record;

import de.sciss.jump3r.lowlevel.LameEncoder;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.entities.Guild;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Receiver implements AudioReceiveHandler{
	private static final Map<Long, Receiver> receivers = new HashMap<>();
	private final LameEncoder encoder;
	private final Path folder;
	private final Guild guild;
	private boolean recording;
	private int packetsHandled;
	private Queue<byte[]> toEncode;
	
	public static Receiver getInstanceOrCreate(@NonNull Guild guild){
		return receivers.computeIfAbsent(guild.getIdLong(), key -> {
			try{
				var receiver = new Receiver(guild);
				guild.getAudioManager().setReceivingHandler(receiver);
				return receiver;
			}
			catch(IOException e){
				Log.getLogger(guild).error("Failed to create receiver", e);
				throw new RuntimeException("Failed to create receiver");
			}
		});
	}
	
	public static Optional<Receiver> getInstance(@NonNull Guild guild){
		return Optional.of(receivers.get(guild.getIdLong()));
	}
	
	private Receiver(Guild guild) throws IOException{
		this.guild = guild;
		this.packetsHandled = 0;
		this.recording = false;
		this.toEncode = new ConcurrentLinkedQueue<>();
		
		folder = Paths.get("rec").resolve(guild.getId());
		Files.createDirectories(folder);
		
		encoder = new LameEncoder(AudioReceiveHandler.OUTPUT_FORMAT, 128, LameEncoder.CHANNEL_MODE_AUTO, LameEncoder.QUALITY_HIGHEST, true);
	}
	
	private void flush(){
		Log.getLogger(guild).info("Flushing audio recording");
		packetsHandled = 0;
		
		try(var fos = Files.newOutputStream(folder.resolve(System.currentTimeMillis() + ".mp3"),
				StandardOpenOption.CREATE,
				StandardOpenOption.WRITE,
				StandardOpenOption.APPEND)){
			byte[] packet;
			while((packet = toEncode.poll()) != null){
				byte[] buffer = new byte[packet.length];
				var encoded = encoder.encodeBuffer(packet, 0, packet.length, buffer);
				fos.write(buffer, 0, encoded);
			}
		}
		catch(IOException e){
			Log.getLogger(guild).error("Failed to encode audio", e);
		}
	}
	
	@Override
	public boolean canReceiveCombined(){
		return recording;
	}
	
	@Override
	public void handleCombinedAudio(@NonNull CombinedAudio combinedAudio){
		toEncode.offer(combinedAudio.getAudioData(1.0f));
		packetsHandled++;
		if(packetsHandled >= 5000){
			flush();
		}
	}
	
	public void stop(){
		this.recording = false;
		flush();
	}
	
	public void start(){
		this.recording = true;
	}
}
