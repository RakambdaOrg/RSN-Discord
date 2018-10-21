package fr.mrcraftcod.gunterdiscord.utils.anilist.media;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-10.
 *
 * @author Thomas Couchoud
 * @since 2018-10-10
 */
public enum AniListMediaSeason{WINTER("Winter"), SPRING("Spring"), SUMMER("Summer"), FALL("Fall");
	
	private final String display;
	
	AniListMediaSeason(final String display){
		this.display = display;
	}
	
	@Override
	public String toString(){
		return this.display;
	}}
