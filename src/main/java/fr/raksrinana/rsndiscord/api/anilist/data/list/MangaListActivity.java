package fr.raksrinana.rsndiscord.api.anilist.data.list;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.NonNull;
import org.apache.commons.lang3.builder.ToStringBuilder;
import java.awt.Color;
import static java.awt.Color.PINK;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("MANGA_LIST")
public class MangaListActivity extends IListActivity{
	public MangaListActivity(){
		super();
	}
	
	@NonNull
	@Override
	protected Color getColor(){
		return PINK;
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}
