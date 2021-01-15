package fr.raksrinana.rsndiscord.api.externaltodos.data;

import lombok.Getter;

@Getter
public enum Kind{
	ANIME(true),
	FEE(false),
	INFORMATION(true),
	MANGA(true),
	PUNISHMENT(false),
	SUGGESTION(true),
	UNKNOWN(true);
	private final boolean cancellable;
	
	Kind(boolean cancellable){
		this.cancellable = cancellable;
	}
}
