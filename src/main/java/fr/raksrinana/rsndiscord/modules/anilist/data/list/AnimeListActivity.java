package fr.raksrinana.rsndiscord.modules.anilist.data.list;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.NonNull;
import org.apache.commons.lang3.builder.ToStringBuilder;
import java.awt.Color;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("ANIME_LIST")
public class AnimeListActivity extends IListActivity{
	public AnimeListActivity(){
		super();
	}
	
	@NonNull
	@Override
	protected Color getColor(){
		return Color.CYAN;
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}
