package fr.rakambda.rsndiscord.spring.interaction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public enum InteractionType{
	BUTTON("button"),
	CONTEXT_MESSAGE("context/message"),
	MODAL("modal"),
	SLASH("slash");
	
	@NotNull
	private final String prefix;
	
	public String prefixed(@NotNull String value){
		return "%s/%s".formatted(getPrefix(), value);
	}
}
