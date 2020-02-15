package fr.raksrinana.rsndiscord.utils.overwatch;

import fr.raksrinana.rsndiscord.utils.overwatch.stage.match.OverwatchMatch;
import fr.raksrinana.utils.http.requestssenders.get.ObjectGetRequestSender;
import kong.unirest.GenericType;
import kong.unirest.Unirest;
import lombok.NonNull;
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
			final var handler = new ObjectGetRequestSender<>(new GenericType<List<OverwatchMap>>(){}, Unirest.get("https://api.overwatchleague.com/maps")).getRequestHandler();
			if(handler.getResult().isSuccess()){
				lastMaps = handler.getRequestResult();
			}
		}
		return Optional.ofNullable(lastMaps).stream().flatMap(Collection::stream).filter(map -> Objects.equals(map.getGuid(), guid)).findFirst();
	}
	
	@NonNull
	public static Optional<OverwatchMatch> getMatch(final int id){
		final var handler = new ObjectGetRequestSender<>(new GenericType<OverwatchMatch>(){}, Unirest.get("https://api.overwatchleague.com/match/{id}?expand=team.content&locale=en_US").routeParam("id", Integer.toString(id)).queryString("expand", "team.content").queryString("locale", "en_US")).getRequestHandler();
		if(handler.getResult().isSuccess()){
			return Optional.of(handler.getRequestResult()).filter(m -> m.getId() > 0);
		}
		return Optional.empty();
	}
	
	@NonNull
	public static Optional<OverwatchResponse> getData(){
		if(Objects.isNull(lastResponse) || System.currentTimeMillis() - lastCheck > DATA_TIMEOUT){
			lastCheck = System.currentTimeMillis();
			final var handler = new ObjectGetRequestSender<>(new GenericType<OverwatchResponse>(){}, Unirest.get("https://api.overwatchleague.com/schedule").queryString("separateStagePlayoffsWeek", true)).getRequestHandler();
			if(handler.getResult().isSuccess()){
				lastResponse = handler.getRequestResult();
			}
		}
		return Optional.ofNullable(lastResponse);
	}
	
	MessageConfiguration.java:67
}
