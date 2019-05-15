package fr.mrcraftcod.gunterdiscord.commands.luxbus;

import fr.mrcraftcod.utils.http.requestssenders.get.StringGetRequestSender;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

public class LuxBusUtils{
	public static Collection<String> getStopIDs() throws MalformedURLException, URISyntaxException{
		final var request = new StringGetRequestSender("http://travelplanner.mobiliteit.lu/hafas/query.exe/dot?performLocating=2&tpl=stop2csv&look_maxdist=150000&look_x=6112550&look_y=49610700&stationProxy=yes").getRequestHandler();
		if(request.getStatus() == 200){
			return List.of(request.getRequestResult().split("\n"));
		}
		return List.of();
	}
}
