package fr.mrcraftcod.gunterdiscord.utils.anilist.list;

import java.awt.*;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-12.
 *
 * @author Thomas Couchoud
 * @since 2018-10-12
 */
public enum AniListMediaListStatus{CURRENT(Color.GREEN), PLANNING(Color.WHITE), COMPLETED(Color.BLACK), DROPPED(Color.RED), PAUSED(Color.ORANGE), REPEATING(Color.YELLOW);
	
	private final Color color;
	
	AniListMediaListStatus(final Color color){
		this.color = color;
	}
	
	public Color getColor(){
		return this.color;
	}}
