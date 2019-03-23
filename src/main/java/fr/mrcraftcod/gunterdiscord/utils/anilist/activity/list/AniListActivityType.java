package fr.mrcraftcod.gunterdiscord.utils.anilist.activity.list;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-10.
 *
 * @author Thomas Couchoud
 * @since 2018-10-10
 */
public enum AniListActivityType{
	ANIME_LIST(AniListAnimeListActivity.class), MANGA_LIST(AniListMangaListActivity.class);

	private final Class<? extends AniListListActivity> klass;
	
	AniListActivityType(final Class<? extends AniListListActivity> klass){
		this.klass = klass;
	}
	
	public AniListListActivity getInstance() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException{
		return getKlass().getConstructor().newInstance();
	}
	
	private Class<? extends AniListListActivity> getKlass(){
		return this.klass;
	}}
