package fr.raksrinana.rsndiscord.utils.discordstatus;

import fr.raksrinana.rsndiscord.utils.discordstatus.data.status.StatusResponse;
import fr.raksrinana.rsndiscord.utils.discordstatus.data.unresolvedincidents.IncidentsReponse;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.utils.http.requestssenders.get.ObjectGetRequestSender;
import kong.unirest.GenericType;
import kong.unirest.Unirest;
import java.util.Optional;

public class DiscordStatus{
	private static final String ENDPOINT = "https://srhpyqt94yxb.statuspage.io/api/v2";
	
	public static Optional<StatusResponse> getStatus(){
		Log.getLogger(null).info("Requesting discord status");
		var request = new ObjectGetRequestSender<>(new GenericType<StatusResponse>(){}, Unirest.get(ENDPOINT + "/status.json")).getRequestHandler();
		if(request.getResult().isSuccess()){
			return Optional.ofNullable(request.getRequestResult());
		}
		Log.getLogger(null).warn("Invalid discord status response");
		return Optional.empty();
	}
	
	public static Optional<IncidentsReponse> getIncidents(){
		Log.getLogger(null).info("Requesting discord incidents");
		var request = new ObjectGetRequestSender<>(new GenericType<IncidentsReponse>(){}, Unirest.get(ENDPOINT + "/incidents.json")).getRequestHandler();
		if(request.getResult().isSuccess()){
			return Optional.ofNullable(request.getRequestResult());
		}
		Log.getLogger(null).warn("Invalid discord incidents response");
		return Optional.empty();
	}
}
