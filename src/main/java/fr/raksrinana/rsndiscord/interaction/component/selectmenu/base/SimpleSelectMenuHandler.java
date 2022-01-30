package fr.raksrinana.rsndiscord.interaction.component.selectmenu.base;

import fr.raksrinana.rsndiscord.interaction.component.selectmenu.api.ISelectMenuHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
public abstract class SimpleSelectMenuHandler implements ISelectMenuHandler{
	private String id;
	
	@Override
	@NotNull
	public String getComponentId(){
		return getId();
	}
}

