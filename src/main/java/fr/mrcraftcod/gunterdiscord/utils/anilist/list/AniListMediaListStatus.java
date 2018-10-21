package fr.mrcraftcod.gunterdiscord.utils.anilist.list;

import java.awt.*;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-12.
 *
 * @author Thomas Couchoud
 * @since 2018-10-12
 */
public enum AniListMediaListStatus{CURRENT(Color.GREEN, ":eyes:"), PLANNING(Color.WHITE, ":soon:"), COMPLETED(Color.BLACK, ":white_check_mark:"), DROPPED(Color.RED, ":no_good:"), PAUSED(Color.ORANGE, ":pause_button:"), REPEATING(Color.YELLOW, ":repeat:");
	
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
