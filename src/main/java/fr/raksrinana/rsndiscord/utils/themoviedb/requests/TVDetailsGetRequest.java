package fr.raksrinana.rsndiscord.utils.themoviedb.requests;

import fr.raksrinana.rsndiscord.utils.themoviedb.TMDBGetRequest;
import fr.raksrinana.rsndiscord.utils.themoviedb.TMDBUtils;
import fr.raksrinana.rsndiscord.utils.themoviedb.model.TVDetails;
import kong.unirest.GenericType;
import kong.unirest.GetRequest;
import kong.unirest.Unirest;

public class TVDetailsGetRequest implements TMDBGetRequest<TVDetails>{
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
		return Unirest.get(TMDBUtils.API_URL + "/tv/{id}").routeParam("id", Long.toString(this.id));
	}
}
