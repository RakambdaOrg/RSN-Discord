package fr.raksrinana.rsndiscord.utils.anilist.activity.list;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.apache.commons.lang3.builder.ToStringBuilder;
import javax.annotation.Nonnull;
import java.awt.Color;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("ANIME_LIST")
public class AniListAnimeListActivity extends AniListListActivity{
	public AniListAnimeListActivity(){
		super();
	}
	
	@Nonnull
	@Override
	protected Color getColor(){
		return Color.CYAN;
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}