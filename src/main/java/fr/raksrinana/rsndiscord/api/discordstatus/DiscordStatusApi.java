package fr.raksrinana.rsndiscord.api.discordstatus;

import fr.raksrinana.rsndiscord.api.discordstatus.data.status.StatusResponse;
import fr.raksrinana.rsndiscord.api.discordstatus.data.unresolvedincidents.IncidentsReponse;
import fr.raksrinana.rsndiscord.log.Log;
import kong.unirest.Unirest;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

public class DiscordStatusApi{
	private static final String ENDPOINT = "https://srhpyqt94yxb.statuspage.io/api/v2";
	
	@NotNull
	public static Optional<IncidentsReponse> getIncidents(){
		Log.getLogger(null).info("Requesting discord incidents");
		var request = Unirest.get(ENDPOINT + "/incidents.json").asObject(IncidentsReponse.class);
		if(request.isSuccess()){
			return ofNullable(request.getBody());
		}
		Log.getLogger(null).warn("Invalid discord incidents response");
		return empty();
	}
	
	@NotNull
	public static Optional<StatusResponse> getStatus(){
		Log.getLogger(null).info("Requesting discord status");
		var request = Unirest.get(ENDPOINT + "/status.json").asObject(StatusResponse.class);
		if(request.isSuccess()){
			return ofNullable(request.getBody());
		}
		Log.getLogger(null).warn("Invalid discord status response");
		return empty();
	}
}
