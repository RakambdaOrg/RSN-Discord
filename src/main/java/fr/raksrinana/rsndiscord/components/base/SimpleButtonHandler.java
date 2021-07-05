package fr.raksrinana.rsndiscord.components.base;

import fr.raksrinana.rsndiscord.components.api.IButtonHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
public abstract class SimpleButtonHandler implements IButtonHandler{
	private String id;
	
	@Override
	@NotNull
	public String getComponentId(){
		return getId();
	}
}

