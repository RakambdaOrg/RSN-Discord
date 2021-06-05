package fr.raksrinana.rsndiscord.button.base;

import fr.raksrinana.rsndiscord.button.api.IButtonHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
public abstract class SimpleButtonHandler implements IButtonHandler{
	private String id;
	
	@Override
	@NotNull
	public String getButtonId(){
		return getId();
	}
}

