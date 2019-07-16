package fr.mrcraftcod.gunterdiscord.utils.overwatch.stage.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverwatchCompetitor{
	@JsonProperty("id")
	private int id;
	@JsonProperty("availableLanguages")
	private List<String> availableLanguages;
	@JsonProperty("handle")
	private String handler;
	@JsonProperty("name")
	private String name;
	@JsonProperty("homeLocation")
	private String homeLocation;
	@JsonProperty("primaryColor") //TODO: Deserializer
	private String primaryColor;
	@JsonProperty("secondaryColor") //TODO: Deserializer
	private String secondaryColor;
	@JsonProperty("game")
	private String game;
	@JsonProperty("abbreviatedName")
	private String abbreviatedName;
	@JsonProperty("addressCountry")
	private String addressCountry;
	@JsonProperty("logo")
	private String logo;
	@JsonProperty("icon")
	private String icon;
	@JsonProperty("secondaryPhoto")
	private String secondaryPhoto;
	@JsonProperty("type")
	private String type;
	
	public String getName(){
		return this.name;
	}
}
