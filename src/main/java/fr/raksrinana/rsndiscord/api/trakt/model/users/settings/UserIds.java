package fr.raksrinana.rsndiscord.api.trakt.model.users.settings;

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
public class UserIds{
	@JsonProperty("slug")
	private String slug;
	
	@Override
	public int hashCode(){
		return Objects.hash(getSlug());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		UserIds userIds = (UserIds) o;
		return Objects.equals(getSlug(), userIds.getSlug());
	}
}
