package fr.mrcraftcod.gunterdiscord.commands.luxbus;

import fr.mrcraftcod.utils.http.requestssenders.get.StringGetRequestSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LuxBusUtils{
	private static final Logger LOGGER = LoggerFactory.getLogger(LuxBusUtils.class);
	public static Map<String, String> stops;
	private static long lastCheck = System.currentTimeMillis();
	
	public static List<String> getStopID(final String stop){
		final var stopKey = stop.toLowerCase();
		return getStopIDs().entrySet().stream().filter(s -> Objects.equals(s.getKey(), stopKey)).map(Map.Entry::getValue).collect(Collectors.toList());
	}
	
	public static Map<String, String> getStopIDs(){
		if(Objects.isNull(stops) || System.currentTimeMillis() - lastCheck > 3600000){
			lastCheck = System.currentTimeMillis();
			try{
				final var request = new StringGetRequestSender("http://travelplanner.mobiliteit.lu/hafas/query.exe/dot?performLocating=2&tpl=stop2csv&look_maxdist=150000&look_x=6112550&look_y=49610700&stationProxy=yes").getRequestHandler();
				if(request.getStatus() == 200){
					stops = Arrays.stream(request.getRequestResult().split("\n")).collect(Collectors.toMap(k -> k.toLowerCase(), Function.identity()));
				}
			}
			catch(URISyntaxException | MalformedURLException e){
				LOGGER.warn("Failed to get bus stops", e);
			}
		}
		return stops;
	}
}
