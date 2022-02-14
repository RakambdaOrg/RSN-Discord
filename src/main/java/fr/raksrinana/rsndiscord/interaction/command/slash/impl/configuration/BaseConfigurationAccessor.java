package fr.raksrinana.rsndiscord.interaction.command.slash.impl.configuration;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public abstract class BaseConfigurationAccessor implements IConfigurationAccessor{
	@Getter
	private final String name;
	@Getter
	private final String topLevel;
	
	public BaseConfigurationAccessor(@NotNull String name){
		this.name = name;
		var index = name.indexOf(".");
		if(index < 0){
			topLevel = name;
		}
		else{
			topLevel = name.substring(0, index);
		}
	}
}
