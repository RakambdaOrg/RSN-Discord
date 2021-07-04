package fr.raksrinana.rsndiscord.button.base;

import fr.raksrinana.rsndiscord.button.api.ISelectionMenuHandler;
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

