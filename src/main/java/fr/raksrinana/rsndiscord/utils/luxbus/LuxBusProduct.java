package fr.raksrinana.rsndiscord.utils.luxbus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class LuxBusProduct{
	@JsonProperty("line")
	private String line;
	@JsonProperty("catCode")
	private String categoryCode;
	@JsonProperty("num")
	private String number;
	@JsonProperty("admin")
	private String admin;
	@JsonProperty("operator")
	private String operator;
	@JsonProperty("operatorCode")
	private String operatorCode;
	@JsonProperty("name")
	private String name;
}
