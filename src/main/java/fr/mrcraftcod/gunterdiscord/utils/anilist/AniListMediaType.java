package fr.mrcraftcod.gunterdiscord.utils.anilist;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-10.
 *
 * @author Thomas Couchoud
 * @since 2018-10-10
 */
public enum AniListMediaType{ANIME("watched episode"), MANGA("read chapter");
	
	private final String type;
	
	AniListMediaType(final String type){
		this.type = type;
	}
	
	public String getProgressType(final boolean several){
		return this.type + (several ? "s" : "");
	}}
