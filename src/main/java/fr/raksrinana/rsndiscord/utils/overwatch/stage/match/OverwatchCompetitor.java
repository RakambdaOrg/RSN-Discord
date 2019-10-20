package fr.raksrinana.rsndiscord.utils.overwatch.stage.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.HexColorDeserializer;
import java.awt.Color;
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
	@JsonProperty("primaryColor")
	@JsonDeserialize(using = HexColorDeserializer.class)
	private Color primaryColor;
	@JsonProperty("secondaryColor")
	@JsonDeserialize(using = HexColorDeserializer.class)
	private Color secondaryColor;
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
	
	public String getIcon(){
		return this.icon;
	}
	
	public int getId(){
		return this.id;
	}
	
	public String getLogo(){
		return this.logo;
	}
	
	public String getName(){
		return this.name;
	}
	
	public Color getPrimaryColor(){
		return this.primaryColor;
	}
}
