package fr.raksrinana.rsndiscord.utils.externaltodos;

import lombok.Getter;

@Getter
public enum Kind{
	UNKNOWN(true), FEE(false), SUGGESTION(true), PUNISHMENT(false), ANIME(true), MANGA(true), INFORMATION(true);
	private final boolean cancellable;
	
	Kind(boolean cancellable){
		this.cancellable = cancellable;
	}
}
