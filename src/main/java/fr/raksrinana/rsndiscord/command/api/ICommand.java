package fr.raksrinana.rsndiscord.command.api;

import org.jetbrains.annotations.NotNull;
import java.util.Map;

public interface ICommand{
	@NotNull
	String getId();
	
	@NotNull
	String getShortDescription();
	
	@NotNull
	Map<String, ? extends ICommand> getCommandMap();
}
