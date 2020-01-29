package fr.raksrinana.rsndiscord.utils.eslgaming.model.generic;

import lombok.Getter;

@Getter
public enum ESLRegion{
	EUROPE(13777), NORTH_AMERICA(13778), LATIN_AMERICA(13779), ANZ(13840), JP(13841), KR(13842), SEA(13843);
	private final int id;
	
	ESLRegion(int id){
		this.id = id;
	}
}
