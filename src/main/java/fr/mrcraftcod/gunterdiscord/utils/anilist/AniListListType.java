package fr.mrcraftcod.gunterdiscord.utils.anilist;

import java.awt.*;

/**
 * Created by Thomas Couchoud (MrCraftCod - zerderr@gmail.com) on 2018-10-10.
 *
 * @author Thomas Couchoud
 * @since 2018-10-10
 */
public enum AniListListType{ANIME_LIST(Color.CYAN), MANGA_LIST(Color.PINK);
	
	private final Color color;
	
	AniListListType(final Color color){
		this.color = color;
	}
	
	public Color getColor(){
		return this.color;
	}}
