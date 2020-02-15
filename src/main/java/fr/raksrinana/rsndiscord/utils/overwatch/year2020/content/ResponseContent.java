package fr.raksrinana.rsndiscord.utils.overwatch.year2020.content;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.meta.Meta;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.week.WeekData;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@Data
public class ResponseContent{
	@JsonProperty("title")
	private String title;
	@JsonProperty("tableData")
	private WeekData weekData;
	@JsonProperty("meta")
	private Meta meta;
}
