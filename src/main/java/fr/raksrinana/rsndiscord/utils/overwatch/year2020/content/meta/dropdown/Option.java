package fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.meta.dropdown;

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
public class Option{
	@JsonProperty("id")
	private String id;
	@JsonProperty("name")
	private String name;
	@JsonProperty("default")
	private boolean defaultSelected;
	@JsonProperty("seriesId")
	private String seriesId;
	
	@Override
	public int hashCode(){
		return Objects.hash(getId());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof Option)){
			return false;
		}
		Option option = (Option) o;
		return Objects.equals(getId(), option.getId());
	}
}
