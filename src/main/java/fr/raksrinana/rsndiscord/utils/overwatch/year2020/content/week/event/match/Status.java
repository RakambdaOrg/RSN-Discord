package fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.week.event.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public enum Status{
	PENDING("Pending"), CONCLUDED("Concluded"), ONGOING("Ongoing");
	private String value;
	
	Status(String value){
		this.value = value;
	}
	
	public String asString(){
		return value;
	}
}
