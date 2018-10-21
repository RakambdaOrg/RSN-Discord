package fr.mrcraftcod.gunterdiscord.utils.anilist.media;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-10.
 *
 * @author Thomas Couchoud
 * @since 2018-10-10
 */
public enum AniListMediaType{ANIME(AniListAnimeMedia.class, true, "Anime"), MANGA(AniListMangaMedia.class, false, "Manga");
	
	private final Class<? extends AniListMedia> klass;
	private final boolean shouldDisplay;
	private final String display;
	
	AniListMediaType(final Class<? extends AniListMedia> klass, final boolean shouldDisplay, final String display){
		this.klass = klass;
		this.shouldDisplay = shouldDisplay;
		this.display = display;
	}
	
	public boolean shouldDisplay(){
		return this.shouldDisplay;
	}
	
	@Override
	public String toString(){
		return this.display;
	}
	
	public AniListMedia getInstance() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException{
		return klass.getConstructor().newInstance();
	}}
