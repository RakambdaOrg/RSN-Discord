package fr.raksrinana.rsndiscord.utils.overwatch.year2019.stage.match;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.raksrinana.rsndiscord.utils.json.HexColorDeserializer;
import lombok.Getter;
import java.awt.Color;
import java.util.Objects;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class OverwatchCompetitor{
	@JsonProperty("id")
	private int id;
	@JsonProperty("availableLanguages")
	private Set<String> availableLanguages;
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
	
	@Override
	public int hashCode(){
		return Objects.hash(id);
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		OverwatchCompetitor that = (OverwatchCompetitor) o;
		return id == that.id;
	}
}
