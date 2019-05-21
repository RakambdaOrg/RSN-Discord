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
public enum AniListMediaSeason{WINTER("Winter"), SPRING("Spring"), SUMMER("Summer"), FALL("Fall");
	
	private final String display;
	
	AniListMediaSeason(final String display){
		this.display = display;
	}
	
	@JsonCreator
	public static AniListMediaSeason getFromString(final String value){
		return AniListMediaSeason.valueOf(value);
	}
	
	@Override
	public String toString(){
		return this.display;
	}}
