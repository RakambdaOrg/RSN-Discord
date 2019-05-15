package fr.mrcraftcod.gunterdiscord.commands.luxbus;

import fr.mrcraftcod.utils.http.requestssenders.get.JSONGetRequestSender;
import fr.mrcraftcod.utils.http.requestssenders.get.StringGetRequestSender;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class LuxBusUtils{
	private static final Logger LOGGER = LoggerFactory.getLogger(LuxBusUtils.class);
	public static Map<String, LuxBusStopID> stops;
	private static long lastCheck = System.currentTimeMillis();
	
	public static List<LuxBusStopID> getStopID(final String stop){
		if(Objects.isNull(stop) || stop.isBlank()){
			throw new IllegalArgumentException("Stop must not be blank");
		}
		final var stopKey = stop.toLowerCase();
		return getStopIDs().entrySet().stream().filter(s -> s.getKey().contains(stopKey)).map(Map.Entry::getValue).collect(Collectors.toList());
	}
	
	public static Map<String, LuxBusStopID> getStopIDs(){
		if(Objects.isNull(stops) || System.currentTimeMillis() - lastCheck > 3600000){
			lastCheck = System.currentTimeMillis();
			try{
				final var request = new StringGetRequestSender("http://travelplanner.mobiliteit.lu/hafas/query.exe/dot?performLocating=2&tpl=stop2csv&look_maxdist=150000&look_x=6112550&look_y=49610700&stationProxy=yes").getRequestHandler();
				if(request.getStatus() == 200){
					stops = Arrays.stream(request.getRequestResult().split("\n")).collect(Collectors.toMap(String::toLowerCase, LuxBusStopID::new));
				}
			}
			catch(URISyntaxException | MalformedURLException e){
				LOGGER.warn("Failed to get bus stops", e);
			}
		}
		return stops;
	}
	
	public static JSONObject getDepartures(final LuxBusStopID stopID){
		try{
			//http://travelplanner.mobiliteit.lu/restproxy/departureBoard?accessId=cdt&id=A=1@O=Belair,%20Sacr%C3%A9-Coeur@X=6,113204@Y=49,610279@U=82@L=200403005@B=1@p=1459856195&format=json
			final var request = new JSONGetRequestSender(String.format("http://travelplanner.mobiliteit.lu/restproxy/departureBoard?accessId=cdt&format=json&id=%s", URLEncoder.encode(stopID.getID(), StandardCharsets.UTF_8))).getRequestHandler();
			if(request.getStatus() == 200){
				return request.getRequestResult();
			}
		}
		catch(URISyntaxException | MalformedURLException e){
			LOGGER.warn("Failed to get bus stop departures (id: {})", stopID, e);
		}
		return null;
	}
}
