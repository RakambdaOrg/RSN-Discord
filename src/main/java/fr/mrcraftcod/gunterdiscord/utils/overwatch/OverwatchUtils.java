package fr.mrcraftcod.gunterdiscord.utils.overwatch;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.mrcraftcod.utils.http.requestssenders.get.JSONGetRequestSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.Optional;

public class OverwatchUtils{
	private static final Logger LOGGER = LoggerFactory.getLogger(OverwatchUtils.class);
	private static long lastCheck = 0;
	private static OverwatchResponse lastResponse;
	
	@Nonnull
	public static Optional<OverwatchResponse> getLastResponse(){
		if(Objects.isNull(lastResponse) || System.currentTimeMillis() - lastCheck > 3600000){
			lastCheck = System.currentTimeMillis();
			try{
				final var json = new JSONGetRequestSender("https://api.overwatchleague.com/schedule").getRequestHandler().getRequestResult();
				lastResponse = new ObjectMapper().readerFor(OverwatchResponse.class).readValue(json.toString());
			}
			catch(URISyntaxException | IOException e){
				LOGGER.error("Failed to get Overwatch data", e);
			}
		}
		return Optional.ofNullable(lastResponse);
	}
}
