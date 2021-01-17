package fr.raksrinana.rsndiscord.api.anilist.data.list;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;
import java.awt.Color;
import static java.awt.Color.CYAN;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("ANIME_LIST")
public class AnimeListActivity extends ListActivity{
	public AnimeListActivity(){
		super();
	}
	
	@NotNull
	@Override
	protected Color getColor(){
		return CYAN;
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}
