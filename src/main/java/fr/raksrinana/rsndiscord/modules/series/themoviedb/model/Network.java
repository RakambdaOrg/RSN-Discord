package fr.raksrinana.rsndiscord.modules.series.themoviedb.model;

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
public class Network{
	@JsonProperty("name")
	private String name;
	@JsonProperty("id")
	private int id;
	@JsonProperty("logo_path")
	private String logoPath;
	@JsonProperty("origin_country")
	private String originCountry;
	
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
		Network network = (Network) o;
		return getId() == network.getId();
	}
}
