package fr.mrcraftcod.gunterdiscord.utils.anilist.activity.list;

import org.apache.commons.lang3.builder.ToStringBuilder;
import java.awt.*;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
public class AniListAnimeListActivity extends AniListListActivity{
	public AniListAnimeListActivity(){
		super(AniListActivityType.ANIME_LIST);
	}
	
	@Override
	protected Color getColor(){
		return Color.CYAN;
	}
	
	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}
}
