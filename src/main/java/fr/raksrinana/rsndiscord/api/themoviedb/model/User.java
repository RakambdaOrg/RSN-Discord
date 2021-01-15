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
public class User{
	@JsonProperty("id")
	private int id;
	@JsonProperty("credit_id")
	private String creditId;
	@JsonProperty("name")
	private String name;
	@JsonProperty("gender")
	private int gender;
	@JsonProperty("profile_path")
	private String profilePath;
	
	@Override
	public int hashCode(){
		return Objects.hash(getId());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		User user = (User) o;
		return getId() == user.getId();
	}
}
