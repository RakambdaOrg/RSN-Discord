package fr.raksrinana.rsndiscord.api.hermitcraft;

import fr.raksrinana.rsndiscord.api.hermitcraft.data.Hermit;
import fr.raksrinana.rsndiscord.api.hermitcraft.data.HermitcraftVideo;
import fr.raksrinana.rsndiscord.utils.Utilities;
import kong.unirest.core.GenericType;
import kong.unirest.core.Unirest;
import lombok.extern.log4j.Log4j2;
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

@Log4j2
public class HermitcraftApi{
	private static final DateTimeFormatter DF = DateTimeFormatter.RFC_1123_DATE_TIME.localizedBy(Locale.ENGLISH);
	private static final String ENDPOINT = "https://hermitcraft.com/api";
	
	@NotNull
	public static Optional<List<Hermit>> getHermits(){
		log.debug("Fetching hermitcraft streams");
		return getRequestResult(new GenericType<>(){}, "/hermit", Map.of());
	}
	
	@NotNull
	private static <T> Optional<T> getRequestResult(@NotNull GenericType<T> type, @NotNull String endpoint, @Nullable Map<String, Object> parameters){
		var request = Unirest.get(ENDPOINT + endpoint).queryString(parameters).asObject(type);
		if(!request.isSuccess()){
			request.getParsingError().ifPresent(error -> {
				Utilities.reportException("Failed to parse Hermitcraft response", error);
				log.warn("Failed to parse Hermitcraft response", error);
			});
			return empty();
		}
		
		return Optional.of(request.getBody());
	}
	
	@NotNull
	public static Optional<List<HermitcraftVideo>> getVideos(){
		log.debug("Fetching Hermitcraft videos");
		return getRequestResult(new GenericType<>(){}, "/videos", Map.of(
				"type", "HermitCraft",
				"start", ZonedDateTime.now(ZoneId.of("UTC")).format(DF)
		));
	}
}
