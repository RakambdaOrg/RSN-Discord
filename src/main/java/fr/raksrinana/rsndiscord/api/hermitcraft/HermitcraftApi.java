package fr.raksrinana.rsndiscord.api.hermitcraft;

import fr.raksrinana.rsndiscord.api.hermitcraft.data.Hermit;
import fr.raksrinana.rsndiscord.api.hermitcraft.data.HermitcraftVideo;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.utils.Utilities;
import kong.unirest.GenericType;
import kong.unirest.Unirest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import static java.util.Optional.empty;

public class HermitcraftApi{
	private static final DateTimeFormatter DF = DateTimeFormatter.RFC_1123_DATE_TIME.localizedBy(Locale.ENGLISH);
	private static final String ENDPOINT = "https://hermitcraft.com/api";
	
	@NotNull
	public static Optional<List<Hermit>> getHermits(){
		Log.getLogger(null).debug("Fetching hermitcraft streams");
		return getRequestResult(new GenericType<>(){}, "/hermit", Map.of());
	}
	
	@NotNull
	private static <T> Optional<T> getRequestResult(@NotNull GenericType<T> type, @NotNull String endpoint, @Nullable Map<String, Object> parameters){
		var request = Unirest.get(ENDPOINT + endpoint).queryString(parameters).asObject(type);
		request.getParsingError().ifPresent(error -> {
			Utilities.reportException("Failed to parse Hermitcraft response", error);
			Log.getLogger(null).warn("Failed to parse Hermitcraft response", error);
		});
		if(request.isSuccess()){
			return Optional.of(request.getBody());
		}
		return empty();
	}
	
	@NotNull
	public static Optional<List<HermitcraftVideo>> getVideos(){
		Log.getLogger(null).debug("Fetching hermitcraft videos");
		return getRequestResult(new GenericType<>(){}, "/videos", Map.of(
				"type", "HermitCraft",
				"start", ZonedDateTime.now(ZoneId.of("UTC")).format(DF)
		));
	}
}
