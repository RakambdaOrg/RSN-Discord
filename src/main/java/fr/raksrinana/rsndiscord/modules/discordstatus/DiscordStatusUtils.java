package fr.raksrinana.rsndiscord.modules.discordstatus;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.discordstatus.data.status.StatusResponse;
import fr.raksrinana.rsndiscord.modules.discordstatus.data.unresolvedincidents.IncidentsReponse;
import kong.unirest.GenericType;
import kong.unirest.Unirest;
import java.util.Optional;

public class DiscordStatusUtils{
	private static final String ENDPOINT = "https://srhpyqt94yxb.statuspage.io/api/v2";
	
	public static Optional<IncidentsReponse> getIncidents(){
		Log.getLogger(null).info("Requesting discord incidents");
		var request = Unirest.get(ENDPOINT + "/incidents.json").asObject(new GenericType<IncidentsReponse>(){});
		if(request.isSuccess()){
			return Optional.ofNullable(request.getBody());
		}
		Log.getLogger(null).warn("Invalid discord incidents response");
		return Optional.empty();
	}
	
	public static Optional<StatusResponse> getStatus(){
		Log.getLogger(null).info("Requesting discord status");
		var request = Unirest.get(ENDPOINT + "/status.json").asObject(new GenericType<StatusResponse>(){});
		if(request.isSuccess()){
			return Optional.ofNullable(request.getBody());
		}
		Log.getLogger(null).warn("Invalid discord status response");
		return Optional.empty();
	}
}
