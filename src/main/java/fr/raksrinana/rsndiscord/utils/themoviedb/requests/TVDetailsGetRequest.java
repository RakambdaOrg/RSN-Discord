package fr.raksrinana.rsndiscord.utils.themoviedb.requests;

import fr.raksrinana.rsndiscord.utils.themoviedb.TMDBGetRequest;
import fr.raksrinana.rsndiscord.utils.themoviedb.model.TVDetails;
import kong.unirest.GenericType;
import lombok.NonNull;
import java.text.MessageFormat;
import java.util.Map;

public class TVDetailsGetRequest implements TMDBGetRequest<TVDetails>{
	private final long id;
	
	public TVDetailsGetRequest(long id){
		this.id = id;
	}
	
	@Override
	public @NonNull String getEndpoint(){
		return MessageFormat.format("/tv/{0}", this.id);
	}
	
	@Override
	public GenericType<? extends TVDetails> getResultClass(){
		return new GenericType<>(){};
	}
	
	@Override
	public Map<String, String> getParameters(){
		return null;
	}
}
