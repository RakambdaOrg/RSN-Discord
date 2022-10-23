package fr.rakambda.rsndiscord.spring.api.themoviedb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User{
	private int id;
	@JsonProperty("credit_id")
	private String creditId;
	private String name;
	private int gender;
	@JsonProperty("profile_path")
	private String profilePath;
}
