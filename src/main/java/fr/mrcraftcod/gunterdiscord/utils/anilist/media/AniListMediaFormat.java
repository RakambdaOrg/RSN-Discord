package fr.mrcraftcod.gunterdiscord.utils.anilist.media;

import java.util.Objects;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-10.
 *
 * @author Thomas Couchoud
 * @since 2018-10-10
 */
public enum AniListMediaFormat{TV("TV"), TV_SHORT("TV Short"), MOVIE("Movie"), SPECIAL("Special"), OVA, ONA, MUSIC("Music"), MANGA("Manga"), NOVEL("Novel"), ONE_SHOT("One shot");
	
	private final String display;
	
	AniListMediaFormat(){
		this(null);
	}
	
	AniListMediaFormat(final String display){
		this.display = display;
	}
	
	@Override
	public String toString(){
		return Objects.isNull(display) ? name() : this.display;
	}}
