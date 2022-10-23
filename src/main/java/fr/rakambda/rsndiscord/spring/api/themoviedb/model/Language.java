package fr.rakambda.rsndiscord.spring.api.themoviedb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Language{
	@JsonProperty("iso_639_1")
	private String iso639_1;
	private String name;
}
