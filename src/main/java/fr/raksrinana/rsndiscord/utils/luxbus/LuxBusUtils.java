package fr.raksrinana.rsndiscord.utils.luxbus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.utils.http.requestssenders.get.JSONGetRequestSender;
import fr.raksrinana.utils.http.requestssenders.get.StringGetRequestSender;
import lombok.NonNull;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class LuxBusUtils{
	private static final int DATA_TIMEOUT = 3600000;
	private static final int HTTP_OK = 200;
	private static final Set<LuxBusStop> stops = new HashSet<>();
	private static long lastCheck = 0;
	
	@NonNull
	public static List<LuxBusStop> searchStopByName(final String stopName){
		if(Objects.isNull(stopName) || stopName.isBlank()){
			throw new IllegalArgumentException("Stop must not be blank");
		}
		final var stopKey = stopName.toLowerCase();
		return getStopIds().stream().filter(stop -> Objects.equals(stopKey, stop.getName().toLowerCase())).findFirst().map(List::of).orElse(getStopIds().stream().filter(stop -> {
			final var compKey = stop.getName().toLowerCase();
			return compKey.startsWith(stopKey) || Arrays.stream(compKey.split(",")).map(String::trim).anyMatch(s2 -> s2.startsWith(stopKey));
		}).collect(Collectors.toList()));
	}
	
	@NonNull
	static Set<LuxBusStop> getStopIds(){
		if(System.currentTimeMillis() - lastCheck > DATA_TIMEOUT){
			lastCheck = System.currentTimeMillis();
			Log.getLogger(null).debug("Fetching bus stop infos");
			try{
				final var request = new StringGetRequestSender("http://travelplanner.mobiliteit.lu/hafas/query.exe/dot?performLocating=2&tpl=stop2csv&look_maxdist=150000&look_x=6112550&look_y=49610700&stationProxy=yes").getRequestHandler();
				if(request.getStatus() == HTTP_OK){
					Arrays.stream(request.getRequestResult().split(";")).map(s -> s.replace("id=", "").replace("\n", "").trim()).filter(s -> !s.isBlank()).map(LuxBusStop::createStop).filter(stop -> !stops.contains(stop)).forEach(stops::add);
				}
			}
			catch(final URISyntaxException | MalformedURLException e){
				Log.getLogger(null).warn("Failed to get bus stops", e);
			}
		}
		return stops;
	}
	
	@NonNull
	public static Set<LuxBusDeparture> getDepartures(@NonNull final LuxBusStop stop){
		try{
			Log.getLogger(null).info("Getting departures for stop {}", stop);
			final var request = new JSONGetRequestSender(String.format("http://travelplanner.mobiliteit.lu/restproxy/departureBoard?accessId=cdt&format=json&id=%s", URLEncoder.encode(stop.getId(), StandardCharsets.UTF_8))).getRequestHandler();
			if(request.getStatus() == HTTP_OK){
				final var response = request.getRequestResult().getObject();
				if(response.has("Departure")){
					return new ObjectMapper().readerFor(new TypeReference<List<LuxBusDeparture>>(){}).readValue(response.getJSONArray("Departure").toString());
				}
			}
			else{
				Log.getLogger(null).warn("Bus API replied with code {}", request.getStatus());
				throw new IllegalStateException("Bus API didn't reply correctly");
			}
		}
		catch(final URISyntaxException | MalformedURLException | JsonProcessingException e){
			Log.getLogger(null).warn("Failed to get bus stop departures (id: {})", stop, e);
		}
		return Set.of();
	}
}
