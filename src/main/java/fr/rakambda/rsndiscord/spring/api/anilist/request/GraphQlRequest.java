package fr.rakambda.rsndiscord.spring.api.anilist.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GraphQlRequest{
	private String query;
	@Builder.Default
	private Map<String, Object> variables = Map.of();
}
