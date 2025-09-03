package fr.rakambda.rsndiscord.spring.api.themoviedb;

import fr.rakambda.rsndiscord.spring.api.HttpUtils;
import fr.rakambda.rsndiscord.spring.api.exceptions.RequestFailedException;
import fr.rakambda.rsndiscord.spring.api.themoviedb.model.MovieDetails;
import fr.rakambda.rsndiscord.spring.api.themoviedb.model.TvDetails;
import fr.rakambda.rsndiscord.spring.settings.ApplicationSettings;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
public class TheMovieDbService{
	private static final String API_URL = "https://api.themoviedb.org/3";
	
	private final WebClient client;
	
	@Autowired
	public TheMovieDbService(ApplicationSettings applicationSettings){
		client = WebClient.builder()
				.baseUrl(API_URL)
				.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + applicationSettings.getTheMovieDb().getAccessToken())
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
				.filter(HttpUtils.logErrorFilter())
				.build();
	}
	
	@NonNull
	public MovieDetails getMovieDetails(long movieId) throws RequestFailedException{
		log.info("Getting movie details for {}", movieId);
		
		try{
			return HttpUtils.withStatusOkAndBody(client.get()
					.uri(b -> b.pathSegment("movie", "{id}").build(movieId))
					.retrieve()
					.toEntity(MovieDetails.class)
					.blockOptional()
					.orElseThrow(() -> new RequestFailedException("Failed to get movie details")));
		}
		catch(WebClientResponseException e){
			log.error("Failed to get movie details", e);
			throw new RequestFailedException("Failed to get movie details");
		}
	}
	
	@NonNull
	public TvDetails getTvDetails(long tvId) throws RequestFailedException{
		
		log.info("Getting tv details for {}", tvId);
		try{
			return HttpUtils.withStatusOkAndBody(client.get()
					.uri(b -> b.pathSegment("tv", "{id}").build(tvId))
					.retrieve()
					.toEntity(TvDetails.class)
					.blockOptional()
					.orElseThrow(() -> new RequestFailedException("Failed to get tv details")));
		}
		catch(WebClientResponseException e){
			log.error("Failed to get tv details", e);
			throw new RequestFailedException("Failed to get tv details");
		}
	}
	
	@NonNull
	public String getImageURL(@NonNull String path, @NonNull TmdbImageSize size){
		if(!path.startsWith("/")){
			path = "/" + path;
		}
		return "https://image.tmdb.org/t/p/%s%s".formatted(size.getValue(), path);
	}
}
