package fr.raksrinana.rsndiscord.utils.eslgaming;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.result.ResponseLinks;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.result.ResponseMeta;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class ESLRequestResult<T>{
	@JsonProperty("items")
	private List<T> items;
	@JsonProperty("_links")
	private ResponseLinks _links;
	@JsonProperty("_meta")
	private ResponseMeta _meta;
}
