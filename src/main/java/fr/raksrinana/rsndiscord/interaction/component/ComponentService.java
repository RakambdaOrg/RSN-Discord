package fr.raksrinana.rsndiscord.interaction.component;

import fr.raksrinana.rsndiscord.interaction.component.button.api.ButtonHandler;
import fr.raksrinana.rsndiscord.interaction.component.button.api.IButtonHandler;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.utils.Utilities.getAllAnnotatedWith;

public class ComponentService{
	private static final Map<String, IButtonHandler> buttonHandlers;
	
	static{
		buttonHandlers = getAllAnnotatedWith(ButtonHandler.class, clazz -> (IButtonHandler) clazz.getConstructor().newInstance())
				.collect(Collectors.toMap(IButtonHandler::getComponentId, Function.identity()));
	}
	
	public static Optional<IButtonHandler> getButtonHandler(@NotNull String id){
		return Optional.of(buttonHandlers.get(id));
	}
}
