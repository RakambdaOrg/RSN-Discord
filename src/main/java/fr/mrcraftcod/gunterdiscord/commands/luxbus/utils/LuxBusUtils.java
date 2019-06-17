package fr.mrcraftcod.gunterdiscord.commands.luxbus.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.mrcraftcod.utils.http.requestssenders.get.JSONGetRequestSender;
import fr.mrcraftcod.utils.http.requestssenders.get.StringGetRequestSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class LuxBusUtils{
	private static final Logger LOGGER = LoggerFactory.getLogger(LuxBusUtils.class);
	public static Set<LuxBusStop> stops = new HashSet<>();
	private static long lastCheck = 0;
	
	public static List<LuxBusStop> searchStopByName(final String stopName){
		if(Objects.isNull(stopName) || stopName.isBlank()){
			throw new IllegalArgumentException("Stop must not be blank");
		}
		final var stopKey = stopName.toLowerCase();
		return getStopIDs().stream().filter(stop -> Objects.equals(stopKey, stop.getName().toLowerCase())).findFirst().map(List::of).orElse(getStopIDs().stream().filter(stop -> {
			final var compKey = stop.getName().toLowerCase();
			return compKey.startsWith(stopKey) || Arrays.stream(compKey.split(",")).map(String::trim).anyMatch(s2 -> s2.startsWith(stopKey));
		}).collect(Collectors.toList()));
	}
	
	public static Set<LuxBusStop> getStopIDs(){
		if(System.currentTimeMillis() - lastCheck > 3600000){
			lastCheck = System.currentTimeMillis();
			LOGGER.debug("Fetching bus stop infos");
			try{
				final var request = new StringGetRequestSender("http://travelplanner.mobiliteit.lu/hafas/query.exe/dot?performLocating=2&tpl=stop2csv&look_maxdist=150000&look_x=6112550&look_y=49610700&stationProxy=yes").getRequestHandler();
				if(request.getStatus() == 200){
					Arrays.stream(request.getRequestResult().split(";")).map(s -> s.replace("id=", "").replace("\n", "").trim()).filter(s -> !s.isBlank()).map(LuxBusStop::createStop).filter(stop -> !stops.contains(stop)).forEach(stop -> stops.add(stop));
				}
			}
			catch(final URISyntaxException | MalformedURLException e){
				LOGGER.warn("Failed to get bus stops", e);
			}
		}
		return stops;
	}
	
	public static List<LuxBusDeparture> getDepartures(final LuxBusStop stop){
		try{
			LOGGER.info("Getting departures for stop {}", stop);
			final var request = new JSONGetRequestSender(String.format("http://travelplanner.mobiliteit.lu/restproxy/departureBoard?accessId=cdt&format=json&id=%s", URLEncoder.encode(stop.getID(), StandardCharsets.UTF_8))).getRequestHandler();
			if(request.getStatus() == 200){
				final var response = request.getRequestResult().getObject();
				if(response.has("Departure")){
					final var departuresList = new ArrayList<LuxBusDeparture>();
					final var departures = response.getJSONArray("Departure");
					for(var i = 0; i < departures.length(); i++){
						try{
							final var obj = (LuxBusDeparture) new ObjectMapper().readerFor(LuxBusDeparture.class).readValue(departures.getJSONObject(i).toString());
							departuresList.add(obj);
						}
						catch(final IOException e){
							LOGGER.error("Failed to parse JSON departure", e);
						}
					}
					return departuresList;
				}
			}
			else{
				LOGGER.warn("Bus API replied with code {}", request.getStatus());
				throw new IllegalStateException("Bus API didn't reply correctly");
			}
		}
		catch(final URISyntaxException | MalformedURLException e){
			LOGGER.warn("Failed to get bus stop departures (id: {})", stop, e);
		}
		return List.of();
	}
	
	public static Optional<LuxBusStop> getStopByID(final String id){
		if(Objects.isNull(id) || id.isBlank()){
			throw new IllegalArgumentException("Stop id must not be blank");
		}
		return getStopIDs().stream().filter(stop -> stop.isStop(id)).findFirst();
	}
}
