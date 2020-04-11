package fr.raksrinana.rsndiscord.utils.hermitcraft;

import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.hermitcraft.data.Hermit;
import fr.raksrinana.rsndiscord.utils.hermitcraft.data.HermitcraftVideo;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.utils.http.requestssenders.get.ObjectGetRequestSender;
import kong.unirest.GenericType;
import kong.unirest.Unirest;
import lombok.NonNull;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class HermitcraftUtils{
	private static final DateTimeFormatter DF = DateTimeFormatter.RFC_1123_DATE_TIME.localizedBy(Locale.ENGLISH);
	private static final String ENDPOINT = "https://hermitcraft.com/api";
	
	@NonNull
	public static Optional<List<Hermit>> getHermits(){
		Log.getLogger(null).debug("Fetching hermitcraft streams");
		return getRequestResult(new GenericType<>(){}, "/hermit", Map.of());
	}
	
	private static <T> Optional<T> getRequestResult(GenericType<T> type, String endpoint, Map<String, Object> parameters){
		final var handler = new ObjectGetRequestSender<>(type, Unirest.get(ENDPOINT + endpoint).queryString(parameters)).getRequestHandler();
		handler.getResult().getParsingError().ifPresent(error -> {
			Actions.sendPrivateMessage(Utilities.RAKSRINANA_ACCOUNT, "Failed to parse Hermitcraft response", Utilities.throwableToEmbed(error).build());
			Log.getLogger(null).warn("Failed to parse Hermitcraft response", error);
		});
		if(handler.getResult().isSuccess()){
			return Optional.of(handler.getRequestResult());
		}
		return Optional.empty();
	}
	
	@NonNull
	public static Optional<List<HermitcraftVideo>> getVideos(){
		Log.getLogger(null).debug("Fetching hermitcraft videos");
		return getRequestResult(new GenericType<>(){}, "/videos", Map.of("type", "HermitCraft", "start", ZonedDateTime.now(ZoneId.of("UTC")).format(DF)));
	}
}
