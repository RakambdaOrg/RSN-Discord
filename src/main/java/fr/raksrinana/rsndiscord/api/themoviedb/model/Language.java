package fr.raksrinana.rsndiscord.api.themoviedb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Language{
	@JsonProperty("iso_639_1")
	private String iso639_1;
	@JsonProperty("name")
	private String name;
	
	@Override
	public int hashCode(){
		return Objects.hash(getIso639_1());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		Language language = (Language) o;
		return Objects.equals(getIso639_1(), language.getIso639_1());
	}
}
