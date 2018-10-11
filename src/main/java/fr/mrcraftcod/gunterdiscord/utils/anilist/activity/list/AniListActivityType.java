package fr.mrcraftcod.gunterdiscord.utils.anilist.activity.list;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-10.
 *
 * @author Thomas Couchoud
 * @since 2018-10-10
 */
public enum AniListActivityType{ANIME_LIST(AniListAnimeListActivity.class, Color.CYAN), MANGA_LIST(AniListMangaListActivity.class, Color.PINK);
	
	private final Color color;
	private final Class<? extends AniListListActivity> klass;
	
	AniListActivityType(final Class<? extends AniListListActivity> klass, final Color color){
		this.klass = klass;
		this.color = color;
	}
	
	public Color getColor(){
		return this.color;
	}
	
	public AniListListActivity getInstance() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException{
		return getKlass().getConstructor().newInstance();
	}
	
	private Class<? extends AniListListActivity> getKlass(){
		return this.klass;
	}}
