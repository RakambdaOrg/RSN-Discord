package fr.rakambda.rsndiscord.spring.api.anilist.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.rakambda.rsndiscord.spring.api.anilist.response.gql.Viewer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class ViewerResponse{
	@JsonProperty("Viewer")
	private Viewer viewer;
}
