package fr.rakambda.rsndiscord.spring.api.hermitcraft;

import fr.rakambda.rsndiscord.spring.api.HttpUtils;
import fr.rakambda.rsndiscord.spring.api.exceptions.RequestFailedException;
import fr.rakambda.rsndiscord.spring.api.hermitcraft.data.Hermit;
import fr.rakambda.rsndiscord.spring.api.hermitcraft.data.HermitcraftVideo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class HermitcraftService{
	private static final DateTimeFormatter DF = DateTimeFormatter.RFC_1123_DATE_TIME.localizedBy(Locale.ENGLISH);
	private static final String ENDPOINT = "https://hermitcraft.com/api";
	
	public final WebClient client;
	
	public HermitcraftService(){
		client = WebClient.create(ENDPOINT);
	}
	
	@NotNull
	public List<Hermit> getHermits() throws RequestFailedException{
		log.info("Fetching hermitcraft streams");
		return HttpUtils.withStatusOkAndBody(client.get()
				.uri(b -> b.pathSegment("hermit").build())
				.retrieve()
				.toEntity(new ParameterizedTypeReference<List<Hermit>>(){})
				.blockOptional()
				.orElseThrow(() -> new RequestFailedException("Failed to request hermits")));
	}
	
	@NotNull
	public List<HermitcraftVideo> getVideos() throws RequestFailedException{
		log.info("Fetching Hermitcraft videos");
		
		var startDate = ZonedDateTime.now(ZoneId.of("UTC")).format(DF);
		return HttpUtils.withStatusOkAndBody(client.get()
				.uri(b -> b.pathSegment("videos")
						.queryParam("type", "HermitCraft")
						.queryParam("start", startDate)
						.build())
				.retrieve()
				.toEntity(new ParameterizedTypeReference<List<HermitcraftVideo>>(){})
				.blockOptional()
				.orElseThrow(() -> new RequestFailedException("Failed to request hermit videos")));
	}
}
