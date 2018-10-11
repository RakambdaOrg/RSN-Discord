package fr.mrcraftcod.gunterdiscord.utils.anilist.media;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-10.
 *
 * @author Thomas Couchoud
 * @since 2018-10-10
 */
public enum AniListMediaType{ANIME(AniListAnimeMedia.class), MANGA(AniListMangaMedia.class);
	
	private final Class<? extends AniListMedia> klass;
	
	AniListMediaType(final Class<? extends AniListMedia> klass){
		this.klass = klass;
	}
	
	public AniListMedia getInstance() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException{
		return klass.getConstructor().newInstance();
	}}
