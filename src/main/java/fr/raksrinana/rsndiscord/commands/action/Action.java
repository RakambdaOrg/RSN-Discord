package fr.raksrinana.rsndiscord.commands.action;

import fr.raksrinana.rsndiscord.utils.giphy.data.Rating;
import lombok.Getter;

@Getter
public enum Action{
	SLAP("slap", Rating.PG);
	private String tag;
	private Rating rating;
	
	Action(String tag, Rating rating){
		this.tag = tag;
		this.rating = rating;
	}
	
	public String getTag(){
		return tag;
	}
	
	public Rating getRating(){
		return rating;
	}
}
