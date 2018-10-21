package fr.mrcraftcod.gunterdiscord.utils.anilist.media;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-10.
 *
 * @author Thomas Couchoud
 * @since 2018-10-10
 */
public enum AniListMediaStatus{FINISHED("Finished"), RELEASING("Releasing"), NOT_YET_RELEASED("Not yet released"), CANCELLED("Cancelled");
	
	private final String display;
	
	AniListMediaStatus(final String display){
		this.display = display;
	}
	
	@Override
	public String toString(){
		return this.display;
	}}
