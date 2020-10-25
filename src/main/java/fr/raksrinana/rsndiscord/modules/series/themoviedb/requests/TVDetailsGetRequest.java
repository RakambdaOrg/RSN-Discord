package fr.raksrinana.rsndiscord.modules.series.themoviedb.requests;

import fr.raksrinana.rsndiscord.modules.series.themoviedb.TMDBUtils;
import fr.raksrinana.rsndiscord.modules.series.themoviedb.model.TVDetails;
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
		return Unirest.get(TMDBUtils.API_URL + "/tv/{id}").routeParam("id", Long.toString(this.id));
	}
}
