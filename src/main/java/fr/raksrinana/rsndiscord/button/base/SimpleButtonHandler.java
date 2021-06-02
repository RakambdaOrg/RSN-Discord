package fr.raksrinana.rsndiscord.button.base;

import fr.raksrinana.rsndiscord.button.api.IButtonHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public abstract class SimpleButtonHandler implements IButtonHandler{
	private final UUID id;
	
	public SimpleButtonHandler(){
		this(UUID.randomUUID());
	}
	
	@Override
	@NotNull
	public String getButtonId(){
		return getId().toString();
	}
}

