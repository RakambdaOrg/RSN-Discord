package fr.raksrinana.rsndiscord.utils.giphy.requests;

import fr.raksrinana.rsndiscord.utils.giphy.GiphyGetRequest;
import fr.raksrinana.rsndiscord.utils.giphy.GiphyUtils;
import fr.raksrinana.rsndiscord.utils.giphy.data.RandomGifResponse;
import fr.raksrinana.rsndiscord.utils.giphy.data.Rating;
import kong.unirest.GenericType;
import kong.unirest.GetRequest;
import kong.unirest.Unirest;

public class RandomGifRequest implements GiphyGetRequest<RandomGifResponse>{
	private String tag;
	private final Rating rating;
	
	public RandomGifRequest(String tag, Rating rating){
		this.tag = tag;
		this.rating = rating;
	}
	
	@Override
	public GenericType<RandomGifResponse> getOutputType(){
		return new GenericType<>(){};
	}
	
	@Override
	public GetRequest getRequest(){
		return Unirest.get(GiphyUtils.API_URL + "/gifs/random")
				.queryString("tag", tag)
				.queryString("rating", rating.getValue());
	}
}
