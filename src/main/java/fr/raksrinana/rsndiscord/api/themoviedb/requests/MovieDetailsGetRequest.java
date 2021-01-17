package fr.raksrinana.rsndiscord.api.themoviedb.requests;

import fr.raksrinana.rsndiscord.api.themoviedb.TheMovieDBApi;
import fr.raksrinana.rsndiscord.api.themoviedb.model.MovieDetails;
import kong.unirest.GenericType;
import kong.unirest.GetRequest;
import kong.unirest.Unirest;
import org.jetbrains.annotations.NotNull;

public class MovieDetailsGetRequest implements ITMDBGetRequest<MovieDetails>{
	private final long id;
	
	public MovieDetailsGetRequest(long id){
		this.id = id;
	}
	
	@Override
	@NotNull
	public GenericType<MovieDetails> getOutputType(){
		return new GenericType<>(){};
	}
	
	@Override
	@NotNull
	public GetRequest getRequest(){
		return Unirest.get(TheMovieDBApi.API_URL + "/movie/{id}").routeParam("id", Long.toString(id));
	}
}
