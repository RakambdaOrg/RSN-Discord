package fr.rakambda.rsndiscord.spring.api.anilist.response.gql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Viewer{
	private int id;
	private String name;
}
