package fr.rakambda.rsndiscord.spring.api.themoviedb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Genre{
	private int id;
	private String name;
}
