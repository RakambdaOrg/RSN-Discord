package fr.raksrinana.rsndiscord.api.themoviedb.requests;

import fr.raksrinana.rsndiscord.api.themoviedb.TheMovieDBApi;
import fr.raksrinana.rsndiscord.api.themoviedb.model.TVDetails;
import kong.unirest.core.GenericType;
import kong.unirest.core.GetRequest;
import kong.unirest.core.Unirest;
import org.jetbrains.annotations.NotNull;

public class TVDetailsGetRequest implements ITMDBGetRequest<TVDetails>{
	private final long id;
	
	public TVDetailsGetRequest(long id){
		this.id = id;
	}
	
	@Override
	@NotNull
	public GenericType<TVDetails> getOutputType(){
		return new GenericType<>(){};
	}
	
	@Override
	@NotNull
	public GetRequest getRequest(){
		return Unirest.get(TheMovieDBApi.API_URL + "/tv/{id}").routeParam("id", Long.toString(id));
	}
}
