package fr.mrcraftcod.gunterdiscord.utils.anilist.activity.list;

import org.apache.commons.lang3.builder.ToStringBuilder;
import java.awt.*;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-11.
 *
 * @author Thomas Couchoud
 * @since 2018-10-11
 */
public class AniListMangaListActivity extends AniListListActivity{
	public AniListMangaListActivity(){
		super(AniListActivityType.MANGA_LIST);
	}
	
	@Override
	protected Color getColor(){
		return Color.PINK;
	}
	
	@Override
	public String toString(){
		return new ToStringBuilder(this).toString();
	}
}
