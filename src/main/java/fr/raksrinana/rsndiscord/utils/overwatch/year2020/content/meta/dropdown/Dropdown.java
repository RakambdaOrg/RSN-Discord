package fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.meta.dropdown;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Objects;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Dropdown{
	@JsonProperty("name")
	private String name;
	@JsonProperty("options")
	private Set<Option> options;
	
	@Override
	public int hashCode(){
		return Objects.hash(getName());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof Dropdown)){
			return false;
		}
		Dropdown dropdown = (Dropdown) o;
		return Objects.equals(getName(), dropdown.getName());
	}
}
