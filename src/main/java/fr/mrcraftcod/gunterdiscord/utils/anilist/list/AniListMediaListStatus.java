package fr.mrcraftcod.gunterdiscord.utils.anilist.list;

import java.awt.*;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-12.
 *
 * @author Thomas Couchoud
 * @since 2018-10-12
 */
public enum AniListMediaListStatus{CURRENT(Color.GREEN, "Current"), PLANNING(Color.WHITE, "Planning"), COMPLETED(Color.BLACK, "Completed"), DROPPED(Color.RED, "Dropped"), PAUSED(Color.ORANGE, "Paused"), REPEATING(Color.YELLOW, "Repeating");
	
	private final Color color;
	private final String display;
	
	AniListMediaListStatus(final Color color, final String display){
		this.color = color;
		this.display = display;
	}
	
	@Override
	public String toString(){
		return this.display;
	}
	
	public Color getColor(){
		return this.color;
	}}
