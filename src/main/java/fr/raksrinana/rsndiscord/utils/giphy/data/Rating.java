package fr.raksrinana.rsndiscord.utils.giphy.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public enum Rating{
	G("g"), PG("pg"), PG13("pg-13"), R("r");
	private final String value;
	
	Rating(String value){
		this.value = value;
	}
	
	@JsonCreator
	public static Rating fromValue(String value){
		for(var rating : Rating.values()){
			if(Objects.equals(rating.getValue(), value)){
				return rating;
			}
		}
		return null;
	}
}
