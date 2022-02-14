package fr.raksrinana.rsndiscord.api.discordstatus;

import fr.raksrinana.rsndiscord.api.discordstatus.data.status.StatusResponse;
import fr.raksrinana.rsndiscord.api.discordstatus.data.unresolvedincidents.IncidentsResponse;
import kong.unirest.core.Unirest;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

@Log4j2
public class DiscordStatusApi{
	private static final String ENDPOINT = "https://srhpyqt94yxb.statuspage.io/api/v2";
	
	@NotNull
	public static Optional<IncidentsResponse> getIncidents(){
		log.info("Requesting discord incidents");
		var request = Unirest.get(ENDPOINT + "/incidents.json").asObject(IncidentsResponse.class);
		if(!request.isSuccess()){
			log.warn("Invalid discord incidents response");
			return empty();
		}
		return ofNullable(request.getBody());
	}
	
	@NotNull
	public static Optional<StatusResponse> getStatus(){
		log.info("Requesting discord status");
		var request = Unirest.get(ENDPOINT + "/status.json").asObject(StatusResponse.class);
		if(!request.isSuccess()){
			log.warn("Invalid discord status response");
			return empty();
		}
		return ofNullable(request.getBody());
	}
}
