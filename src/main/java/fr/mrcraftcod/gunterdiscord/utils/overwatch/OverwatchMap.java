package fr.mrcraftcod.gunterdiscord.utils.overwatch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.mrcraftcod.gunterdiscord.utils.json.OverwatchLocalizedStringDeserializer;
import fr.mrcraftcod.gunterdiscord.utils.json.URLDeserializer;
import fr.mrcraftcod.gunterdiscord.utils.overwatch.map.OverwatchGameMode;
import java.net.URL;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverwatchMap{
	@JsonProperty("guid")
	private String guid;
	@JsonProperty("name")
	@JsonDeserialize(using = OverwatchLocalizedStringDeserializer.class)
	private String name;
	@JsonProperty("gameModes")
	private List<OverwatchGameMode> gameModes;
	@JsonProperty("id")
	private String id;
	@JsonProperty("icon")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL icon;
	@JsonProperty("thumbnail")
	@JsonDeserialize(using = URLDeserializer.class)
	private URL thumbnail;
	@JsonProperty("type")
	private String type;
	
	@Override
	public String toString(){
		return this.getName();
	}
	
	private String getName(){
		return this.name;
	}
	
	public String getGuid(){
		return this.guid;
	}
	
	public String getType(){
		return this.type;
	}
}
