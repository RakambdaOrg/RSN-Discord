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
public class ProductionCountry{
	@JsonProperty("iso_3166_1")
	private String iso3166_1;
	@JsonProperty("name")
	private String name;
	
	@Override
	public int hashCode(){
		return Objects.hash(getIso3166_1());
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		ProductionCountry that = (ProductionCountry) o;
		return Objects.equals(getIso3166_1(), that.getIso3166_1());
	}
}
