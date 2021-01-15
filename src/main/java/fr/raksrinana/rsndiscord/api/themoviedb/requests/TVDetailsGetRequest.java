package fr.raksrinana.rsndiscord.api.themoviedb.requests;

import fr.raksrinana.rsndiscord.api.themoviedb.TheMovieDBApi;
import fr.raksrinana.rsndiscord.api.themoviedb.model.TVDetails;
import kong.unirest.GenericType;
import kong.unirest.GetRequest;
import kong.unirest.Unirest;

public class TVDetailsGetRequest implements ITMDBGetRequest<TVDetails>{
	private final long id;
	
	public TVDetailsGetRequest(long id){
		this.id = id;
	}
	
	@Override
	public GenericType<TVDetails> getOutputType(){
		return new GenericType<>(){};
	}
	
	@Override
	public GetRequest getRequest(){
		return Unirest.get(TheMovieDBApi.API_URL + "/tv/{id}").routeParam("id", Long.toString(this.id));
	}
}
