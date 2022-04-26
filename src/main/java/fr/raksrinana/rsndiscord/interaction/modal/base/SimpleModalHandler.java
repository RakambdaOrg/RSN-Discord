package fr.raksrinana.rsndiscord.interaction.modal.base;

import fr.raksrinana.rsndiscord.interaction.modal.api.IModalHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
public abstract class SimpleModalHandler implements IModalHandler{
	private String id;
	
	@Override
	@NotNull
	public String getComponentId(){
		return getId();
	}
}

