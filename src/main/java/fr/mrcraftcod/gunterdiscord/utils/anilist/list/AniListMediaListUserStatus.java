package fr.mrcraftcod.gunterdiscord.utils.anilist.list;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.awt.Color;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-12.
 *
 * @author Thomas Couchoud
 * @since 2018-10-12
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum AniListMediaListUserStatus{
	CURRENT(Color.GREEN, "Current"), PLANNING(Color.WHITE, "Planning"), COMPLETED(Color.BLACK, "Completed"), DROPPED(Color.RED, "Dropped"), PAUSED(Color.ORANGE, "Paused"), REPEATING(Color.YELLOW, "Repeating"), UNKNOWN(Color.MAGENTA, "Unknown");
	
	private final Color color;
	private final String display;
	
	AniListMediaListUserStatus(final Color color, final String display){
		this.color = color;
		this.display = display;
	}
	
	@JsonCreator
	public static AniListMediaListUserStatus getFromString(final String value){
		return AniListMediaListUserStatus.valueOf(value);
	}
	
	@Override
	public String toString(){
		return this.display;
	}
	
	public Color getColor(){
		return this.color;
	}}
