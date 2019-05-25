package fr.mrcraftcod.gunterdiscord.utils.anilist.media;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-10.
 *
 * @author Thomas Couchoud
 * @since 2018-10-10
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum AniListMediaStatus{FINISHED("Finished"), RELEASING("Releasing"), NOT_YET_RELEASED("Not yet released"), CANCELLED("Cancelled");
	
	private final String display;
	
	AniListMediaStatus(final String display){
		this.display = display;
	}
	
	@JsonCreator
	public static AniListMediaStatus getFromString(final String value){
		return AniListMediaStatus.valueOf(value);
	}
	
	@Override
	public String toString(){
		return this.display;
	}}
