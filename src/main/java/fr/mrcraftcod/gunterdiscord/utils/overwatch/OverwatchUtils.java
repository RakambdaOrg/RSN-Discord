package fr.mrcraftcod.gunterdiscord.utils.overwatch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.match.OverwatchMatch;
import fr.mrcraftcod.utils.http.requestssenders.get.JSONGetRequestSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class OverwatchUtils{
	private static final Logger LOGGER = LoggerFactory.getLogger(OverwatchUtils.class);
	private static long lastCheck = 0;
	private static long lastCheckMaps = 0;
	private static OverwatchResponse lastResponse;
	private static List<OverwatchMap> lastMaps;
	
	@Nonnull
	public static Optional<OverwatchMap> getMap(String guid){
		//
		if(Objects.isNull(lastMaps) || System.currentTimeMillis() - lastCheckMaps > 3600000){
			lastCheckMaps = System.currentTimeMillis();
			try{
				final var json = new JSONGetRequestSender("https://api.overwatchleague.com/maps").getRequestHandler().getRequestResult();
				lastMaps = new ObjectMapper().readerFor(new TypeReference<List<OverwatchMap>>(){}).readValue(json.toString());
			}
			catch(URISyntaxException | IOException e){
				LOGGER.error("Failed to get Overwatch maps", e);
			}
		}
		return Optional.ofNullable(lastMaps).stream().flatMap(List::stream).filter(map -> Objects.equals(map.getGuid(), guid)).findFirst();
	}
	
	@Nonnull
	public static Optional<OverwatchMatch> getMatch(int id){
		try{
			final var json = new JSONGetRequestSender(String.format("https://api.overwatchleague.com/match/%d?expand=team.content&locale=en_US", id)).getRequestHandler().getRequestResult();
			return Optional.<OverwatchMatch> ofNullable(new ObjectMapper().readerFor(OverwatchMatch.class).readValue(json.toString())).filter(m -> m.getId() > 0);
		}
		catch(URISyntaxException | IOException e){
			LOGGER.error("Failed to get Overwatch match", e);
		}
		return Optional.empty();
	}
	
	@Nonnull
	public static Optional<OverwatchResponse> getLastResponse(){
		if(Objects.isNull(lastResponse) || System.currentTimeMillis() - lastCheck > 120){
			lastCheck = System.currentTimeMillis();
			try{
				final var json = new JSONGetRequestSender("https://api.overwatchleague.com/schedule?separateStagePlayoffsWeek=true").getRequestHandler().getRequestResult();
				lastResponse = new ObjectMapper().readerFor(OverwatchResponse.class).readValue(json.toString());
			}
			catch(URISyntaxException | IOException e){
				LOGGER.error("Failed to get Overwatch data", e);
			}
		}
		return Optional.ofNullable(lastResponse);
	}
}
