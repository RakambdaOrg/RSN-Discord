package fr.mrcraftcod.gunterdiscord.utils.player.sourcemanagers.httpfolder;

import com.sedmelluq.discord.lavaplayer.container.*;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.ProbingAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.io.*;
import com.sedmelluq.discord.lavaplayer.track.*;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import static com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity.COMMON;
import static com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity.SUSPICIOUS;
import static com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools.getHeaderValue;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com)
 *
 * @author Thomas Couchoud
 * @since 2018-08-24
 */
public class HttpFolderAudioSourceManager extends ProbingAudioSourceManager implements HttpConfigurable{
	private final static Logger LOGGER = LoggerFactory.getLogger(HttpFolderAudioSourceManager.class);
	private final HttpInterfaceManager httpInterfaceManager;
	
	public HttpFolderAudioSourceManager(){
		httpInterfaceManager = new ThreadLocalHttpInterfaceManager(HttpClientTools.createSharedCookiesHttpBuilder().setRedirectStrategy(new HttpClientTools.NoRedirectsStrategy()), HttpClientTools.DEFAULT_REQUEST_CONFIG);
	}
	
	@Override
	protected AudioTrack createTrack(final AudioTrackInfo trackInfo, final MediaContainerProbe probe){
		return new HttpFolderAudioTrack(trackInfo, probe, this);
	}
	
	@Override
	public void configureRequests(final Function<RequestConfig, RequestConfig> configurator){
		httpInterfaceManager.configureRequests(configurator);
	}
	
	@Override
	public void configureBuilder(final Consumer<HttpClientBuilder> configurator){
		httpInterfaceManager.configureBuilder(configurator);
	}
	
	@Override
	public String getSourceName(){
		return "httpfolder";
	}
	
	@Override
	public AudioItem loadItem(final DefaultAudioPlayerManager manager, final AudioReference reference){
		final var httpReference = getAsHttpReference(reference);
		if(httpReference == null){
			return null;
		}
		
		try{
			return loadPlaylist(httpReference);
		}
		catch(final IOException e){
			LOGGER.error("Error getting http folder playlist", e);
		}
		return null;
	}
	
	private AudioReference getAsHttpReference(final AudioReference reference){
		if(reference.identifier.startsWith("mcc://")){
			return new AudioReference(reference.identifier.substring("mcc://".length()), reference.title);
		}
		return null;
	}
	
	private AudioPlaylist loadPlaylist(final AudioReference httpReference) throws IOException{
		final var url = new URL(httpReference.identifier);
		final var root = Jsoup.parse(url, 10000);
		final var tracks = root.getElementsByTag("a").stream().map(a -> {
			var href = a.attr("href");
			if(href.startsWith("http://") || href.startsWith("https://")){
				return href;
			}
			else if(href.startsWith("/")){
				return url.getProtocol() + "://" + url.getHost() + href;
			}
			return url.toString() + href;
		}).distinct().map(trackUrl -> new AudioReference(trackUrl, URLDecoder.decode(StringEscapeUtils.unescapeHtml4(trackUrl.substring(trackUrl.lastIndexOf("/") + 1)), StandardCharsets.UTF_8))).map(trackHttpReference -> {
			try{
				return handleLoadResult(detectContainer(trackHttpReference));
			}
			catch(final Exception ignored){
			}
			return null;
		}).filter(audioItem -> audioItem instanceof AudioTrack).map(audioItem -> (AudioTrack) audioItem).collect(Collectors.toList());
		return new BasicAudioPlaylist(httpReference.title, tracks, tracks.stream().findFirst().orElse(null), false);
	}
	
	private MediaContainerDetectionResult detectContainer(final AudioReference reference){
		final MediaContainerDetectionResult result;
		
		try(final var httpInterface = getHttpInterface()){
			result = detectContainerWithClient(httpInterface, reference);
		}
		catch(final IOException e){
			throw new FriendlyException("Connecting to the URL failed.", SUSPICIOUS, e);
		}
		
		return result;
	}
	
	/**
	 * @return Get an HTTP interface for a playing track.
	 */
	public HttpInterface getHttpInterface(){
		return httpInterfaceManager.getInterface();
	}
	
	private MediaContainerDetectionResult detectContainerWithClient(final HttpInterface httpInterface, final AudioReference reference) throws IOException{
		try(final var inputStream = new PersistentHttpStream(httpInterface, new URI(reference.identifier), Long.MAX_VALUE)){
			final var statusCode = inputStream.checkStatusCode();
			final var redirectUrl = HttpClientTools.getRedirectLocation(reference.identifier, inputStream.getCurrentResponse());
			
			if(redirectUrl != null){
				return new MediaContainerDetectionResult(null, new AudioReference(redirectUrl, null));
			}
			else if(statusCode == HttpStatus.SC_NOT_FOUND){
				return null;
			}
			else if(!HttpClientTools.isSuccessWithContent(statusCode)){
				throw new FriendlyException("That URL is not playable.", COMMON, new IllegalStateException("Status code " + statusCode));
			}
			
			final var hints = MediaContainerHints.from(getHeaderValue(inputStream.getCurrentResponse(), "Content-Type"), null);
			return MediaContainerDetection.detectContainer(reference, inputStream, hints);
		}
		catch(final URISyntaxException e){
			throw new FriendlyException("Not a valid URL.", COMMON, e);
		}
	}
	
	@Override
	public boolean isTrackEncodable(final AudioTrack track){
		return true;
	}
	
	@Override
	public void encodeTrack(final AudioTrack track, final DataOutput output) throws IOException{
		output.writeUTF(((HttpFolderAudioTrack) track).getProbe().getName());
	}
	
	@Override
	public AudioTrack decodeTrack(final AudioTrackInfo trackInfo, final DataInput input) throws IOException{
		final var probeName = input.readUTF();
		return Arrays.stream(MediaContainer.class.getEnumConstants()).filter(container -> container.probe.getName().equals(probeName)).findFirst().map(container -> new HttpFolderAudioTrack(trackInfo, container.probe, this)).orElse(null);
	}
	
	@Override
	public void shutdown(){
		// Nothing to shut down
	}
}
