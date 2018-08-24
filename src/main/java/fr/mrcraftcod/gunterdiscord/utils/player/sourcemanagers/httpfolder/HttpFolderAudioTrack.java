package fr.mrcraftcod.gunterdiscord.utils.player.sourcemanagers.httpfolder;

import com.sedmelluq.discord.lavaplayer.container.MediaContainerProbe;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import com.sedmelluq.discord.lavaplayer.tools.io.PersistentHttpStream;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.DelegatedAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.InternalAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;

/**
 * Audio track that handles processing Youtube videos as audio tracks.
 */
public class HttpFolderAudioTrack extends DelegatedAudioTrack{
	private static final Logger log = LoggerFactory.getLogger(HttpFolderAudioTrack.class);
	
	private final MediaContainerProbe probe;
	private final HttpFolderAudioSourceManager sourceManager;
	
	/**
	 * @param trackInfo     Track info
	 * @param sourceManager Source manager which was used to find this track
	 */
	public HttpFolderAudioTrack(AudioTrackInfo trackInfo, MediaContainerProbe probe, HttpFolderAudioSourceManager sourceManager){
		super(trackInfo);
		
		this.probe = probe;
		this.sourceManager = sourceManager;
	}
	
	@Override
	public void process(LocalAudioTrackExecutor localExecutor) throws Exception{
		try(HttpInterface httpInterface = sourceManager.getHttpInterface()){
			log.debug("Starting http track from URL: {}", trackInfo.identifier);
			
			try(PersistentHttpStream inputStream = new PersistentHttpStream(httpInterface, new URI(trackInfo.identifier), Long.MAX_VALUE)){
				processDelegate((InternalAudioTrack) probe.createTrack(trackInfo, inputStream), localExecutor);
			}
		}
	}
	
	@Override
	public AudioTrack makeClone(){
		return new HttpFolderAudioTrack(trackInfo, probe, sourceManager);
	}
	
	@Override
	public AudioSourceManager getSourceManager(){
		return sourceManager;
	}
	
	/**
	 * @return The media probe which handles creating a container-specific delegated track for this track.
	 */
	public MediaContainerProbe getProbe(){
		return probe;
	}
}
