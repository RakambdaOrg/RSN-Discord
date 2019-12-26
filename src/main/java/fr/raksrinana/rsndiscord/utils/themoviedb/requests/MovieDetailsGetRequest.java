package fr.raksrinana.rsndiscord.utils.themoviedb.requests;

import fr.raksrinana.rsndiscord.utils.themoviedb.TMDBGetRequest;
import fr.raksrinana.rsndiscord.utils.themoviedb.model.MovieDetails;
import kong.unirest.GenericType;
import lombok.NonNull;
import java.text.MessageFormat;
import java.util.Map;

public class MovieDetailsGetRequest implements TMDBGetRequest<MovieDetails>{
	private final long id;
	
	public MovieDetailsGetRequest(long id){
		this.id = id;
	}
	
	@Override
	public @NonNull String getEndpoint(){
		return MessageFormat.format("/movie/{0}", this.id);
	}
	
	@Override
	public GenericType<? extends MovieDetails> getResultClass(){
		return new GenericType<>(){};
	}
	
	@Override
	public Map<String, String> getParameters(){
		return null;
	}
}
