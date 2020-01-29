package fr.raksrinana.rsndiscord.utils.themoviedb.requests;

import fr.raksrinana.rsndiscord.utils.themoviedb.TMDBGetRequest;
import fr.raksrinana.rsndiscord.utils.themoviedb.TMDBUtils;
import fr.raksrinana.rsndiscord.utils.themoviedb.model.MovieDetails;
import kong.unirest.GenericType;
import kong.unirest.GetRequest;
import kong.unirest.Unirest;

public class MovieDetailsGetRequest implements TMDBGetRequest<MovieDetails>{
	private final long id;
	
	public MovieDetailsGetRequest(long id){
		this.id = id;
	}
	
	@Override
	public GenericType<MovieDetails> getOutputType(){
		return new GenericType<>(){};
	}
	
	@Override
	public GetRequest getRequest(){
		return Unirest.get(TMDBUtils.API_URL + "/movie/{id}").routeParam("id", Long.toString(this.id));
	}
}
