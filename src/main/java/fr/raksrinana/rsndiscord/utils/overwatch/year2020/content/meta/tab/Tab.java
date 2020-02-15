package fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.meta.tab;

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
public class Tab{
	@JsonProperty("id")
	private int id;
	@JsonProperty("stage")
	private String stage;
	@JsonProperty("name")
	private String name;
	@JsonProperty("selected")
	private boolean selected;
	@JsonProperty("subtabs")
	private Set<SubTab> subtabs;
	@JsonProperty("options")
	private Options options;
	
	@Override
	public int hashCode(){
		return Objects.hash(getId());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof Tab)){
			return false;
		}
		Tab tab = (Tab) o;
		return getId() == tab.getId();
	}
}
