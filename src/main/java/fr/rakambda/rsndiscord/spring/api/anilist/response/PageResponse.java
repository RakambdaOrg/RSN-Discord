package fr.rakambda.rsndiscord.spring.api.anilist.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponse<T extends PagedResponse> {
	@JsonProperty("Page")
	private T page;
}
