package fr.raksrinana.rsndiscord.components;

import fr.raksrinana.rsndiscord.components.api.IButtonHandler;
import fr.raksrinana.rsndiscord.components.api.ISelectionMenuHandler;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.utils.Utilities.getAllAnnotatedWith;

public class ComponentService{
	private static final Map<String, IButtonHandler> buttonHandlers;
	private static final Map<String, ISelectionMenuHandler> selectionMenuHandlers;
	static{
		buttonHandlers = getAllAnnotatedWith(ButtonHandler.class, clazz -> (IButtonHandler) clazz.getConstructor().newInstance())
				.collect(Collectors.toMap(IButtonHandler::getComponentId, Function.identity()));
		selectionMenuHandlers = getAllAnnotatedWith(SelectionMenuHandler.class, clazz -> (ISelectionMenuHandler) clazz.getConstructor().newInstance())
				.collect(Collectors.toMap(ISelectionMenuHandler::getComponentId, Function.identity()));
	}
	public static Optional<IButtonHandler> getButtonHandler(@NotNull String id){
		return Optional.of(buttonHandlers.get(id));
	}
	
	public static Optional<ISelectionMenuHandler> getSelectionMenuHandler(@NotNull String id){
		return Optional.of(selectionMenuHandlers.get(id));
	}
}
