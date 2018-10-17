package fr.mrcraftcod.gunterdiscord.utils.anilist.media;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-10.
 *
 * @author Thomas Couchoud
 * @since 2018-10-10
 */
public enum AniListMediaType{ANIME(AniListAnimeMedia.class, false), MANGA(AniListMangaMedia.class, true);
	
	private final Class<? extends AniListMedia> klass;
	private final boolean shouldDisplay;
	
	AniListMediaType(final Class<? extends AniListMedia> klass, final boolean shouldDisplay){
		this.klass = klass;
		this.shouldDisplay = shouldDisplay;
	}
	
	public boolean shouldDisplay(){
		return this.shouldDisplay;
	}
	
	public AniListMedia getInstance() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException{
		return klass.getConstructor().newInstance();
	}}
