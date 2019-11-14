package fr.raksrinana.rsndiscord.utils.overwatch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.mrcraftcod.utils.http.requestssenders.get.JSONGetRequestSender;
import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.rsndiscord.utils.overwatch.stage.match.OverwatchMatch;
import lombok.NonNull;
import org.apache.commons.lang3.exception.ExceptionUtils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class OverwatchUtils{
	private static final int MAP_DATA_TIMEOUT = 3600000;
	private static final int DATA_TIMEOUT = 120000;
	private static long lastCheck = 0;
	private static long lastCheckMaps = 0;
	private static OverwatchResponse lastResponse;
	private static Collection<OverwatchMap> lastMaps;
	
	@NonNull
	public static Optional<OverwatchMap> getMap(final String guid){
		if(Objects.isNull(lastMaps) || System.currentTimeMillis() - lastCheckMaps > MAP_DATA_TIMEOUT){
			lastCheckMaps = System.currentTimeMillis();
			try{
				final var json = new JSONGetRequestSender("https://api.overwatchleague.com/maps").getRequestHandler().getRequestResult();
				lastMaps = new ObjectMapper().readerFor(new TypeReference<List<OverwatchMap>>(){}).readValue(json.toString());
			}
			catch(final URISyntaxException | IOException e){
				Log.getLogger(null).error("Failed to get Overwatch maps", e);
			}
		}
		return Optional.ofNullable(lastMaps).stream().flatMap(Collection::stream).filter(map -> Objects.equals(map.getGuid(), guid)).findFirst();
	}
	
	@NonNull
	public static Optional<OverwatchMatch> getMatch(final int id){
		try{
			final var json = new JSONGetRequestSender(String.format("https://api.overwatchleague.com/match/%d?expand=team.content&locale=en_US", id)).getRequestHandler().getRequestResult();
			return Optional.<OverwatchMatch> ofNullable(new ObjectMapper().enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE).readerFor(OverwatchMatch.class).readValue(json.toString())).filter(m -> m.getId() > 0);
		}
		catch(final URISyntaxException | IOException e){
			Log.getLogger(null).error("Failed to get Overwatch match", e);
		}
		return Optional.empty();
	}
	
	@NonNull
	public static Optional<OverwatchResponse> getData(){
		if(Objects.isNull(lastResponse) || System.currentTimeMillis() - lastCheck > DATA_TIMEOUT){
			lastCheck = System.currentTimeMillis();
			try{
				final var json = new JSONGetRequestSender("https://api.overwatchleague.com/schedule?separateStagePlayoffsWeek=true").getRequestHandler().getRequestResult();
				lastResponse = new ObjectMapper().readerFor(OverwatchResponse.class).readValue(json.toString());
			}
			catch(final URISyntaxException | IOException e){
				Log.getLogger(null).error("Failed to get Overwatch data", e);
				Optional.ofNullable(Main.getJda().getUserById(Utilities.RAKSRINANA_ACCOUNT)).ifPresent(user -> user.openPrivateChannel().queue(chan -> chan.sendMessage("Overwatch problem: " + e.toString() + "\n" + ExceptionUtils.getStackTrace(e)).queue()));
			}
		}
		return Optional.ofNullable(lastResponse);
	}
}
