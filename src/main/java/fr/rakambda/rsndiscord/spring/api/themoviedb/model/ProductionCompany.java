package fr.rakambda.rsndiscord.spring.api.themoviedb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductionCompany{
	private String name;
	private int id;
	@JsonProperty("logo_path")
	private String logoPath;
	@JsonProperty("origin_country")
	private String originCountry;
}
