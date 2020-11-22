package fr.raksrinana.rsndiscord.modules.hermitcraft;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.hermitcraft.data.Hermit;
import fr.raksrinana.rsndiscord.modules.hermitcraft.data.HermitcraftVideo;
import fr.raksrinana.rsndiscord.utils.Utilities;
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
	
	@NonNull
	public static Optional<List<HermitcraftVideo>> getVideos(){
		Log.getLogger(null).debug("Fetching hermitcraft videos");
		return getRequestResult(new GenericType<>(){}, "/videos", Map.of(
				"type", "HermitCraft",
				"start", ZonedDateTime.now(ZoneId.of("UTC")).format(DF)
		));
	}
	
	private static <T> Optional<T> getRequestResult(GenericType<T> type, String endpoint, Map<String, Object> parameters){
		var request = Unirest.get(ENDPOINT + endpoint).queryString(parameters).asObject(type);
		request.getParsingError().ifPresent(error -> {
			Utilities.reportException("Failed to parse Hermitcraft response", error);
			Log.getLogger(null).warn("Failed to parse Hermitcraft response", error);
		});
		if(request.isSuccess()){
			return Optional.of(request.getBody());
		}
		return Optional.empty();
	}
}
