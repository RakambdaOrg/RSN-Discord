package fr.mrcraftcod.gunterdiscord.utils.anilist.activity.list;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-10.
 *
 * @author Thomas Couchoud
 * @since 2018-10-10
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum AniListActivityType{
	ANIME_LIST(AniListAnimeListActivity.class), MANGA_LIST(AniListMangaListActivity.class);
	
	private final Class<? extends AniListListActivity> klass;
	
	AniListActivityType(final Class<? extends AniListListActivity> klass){
		this.klass = klass;
	}
	
	public AniListListActivity getInstance() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException{
		return getKlass().getConstructor().newInstance();
	}
	
	@JsonCreator
	public static AniListActivityType getFromString(final String value){
		return AniListActivityType.valueOf(value);
	}
	
	private Class<? extends AniListListActivity> getKlass(){
		return this.klass;
	}
}
