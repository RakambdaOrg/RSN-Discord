package fr.raksrinana.rsndiscord.components.base;

import fr.raksrinana.rsndiscord.components.api.ISelectionMenuHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
public abstract class SimpleSelectionMenuHandler implements ISelectionMenuHandler{
	private String id;
	
	@Override
	@NotNull
	public String getComponentId(){
		return getId();
	}
}

