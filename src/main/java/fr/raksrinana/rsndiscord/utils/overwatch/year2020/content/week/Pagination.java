package fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.week;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class Pagination{
	@JsonProperty("currentPage")
	private int currentPage;
	@JsonProperty("totalPages")
	private int totalPages;
	@JsonProperty("nextPage")
	private Integer nextPage;
	@JsonProperty("previousPage")
	private Integer previousPage;
	@JsonProperty("perPage")
	private int perPage;
}
