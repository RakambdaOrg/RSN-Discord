package fr.rakambda.rsndiscord.spring.interaction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;

@Getter
@RequiredArgsConstructor
public enum InteractionType{
	BUTTON("button"),
	CONTEXT_MESSAGE("context/message"),
	MODAL("modal"),
	SLASH("slash");
	
	@NonNull
	private final String prefix;
	
	public String prefixed(@NonNull String value){
		return "%s/%s".formatted(getPrefix(), value);
	}
}
