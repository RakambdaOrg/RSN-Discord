package fr.raksrinana.rsndiscord.interaction.component;

import fr.raksrinana.rsndiscord.interaction.component.button.api.ButtonHandler;
import fr.raksrinana.rsndiscord.interaction.component.button.api.IButtonHandler;
import fr.raksrinana.rsndiscord.interaction.component.selectmenu.api.SelectionMenuHandler;
import fr.raksrinana.rsndiscord.interaction.component.selectmenu.api.ISelectMenuHandler;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.utils.Utilities.getAllAnnotatedWith;

public class ComponentService{
	private static final Map<String, IButtonHandler> buttonHandlers;
	private static final Map<String, ISelectMenuHandler> selectionMenuHandlers;
	
	static{
		buttonHandlers = getAllAnnotatedWith(ButtonHandler.class, clazz -> (IButtonHandler) clazz.getConstructor().newInstance())
				.collect(Collectors.toMap(IButtonHandler::getComponentId, Function.identity()));
		selectionMenuHandlers = getAllAnnotatedWith(SelectionMenuHandler.class, clazz -> (ISelectMenuHandler) clazz.getConstructor().newInstance())
				.collect(Collectors.toMap(ISelectMenuHandler::getComponentId, Function.identity()));
	}
	
	public static Optional<IButtonHandler> getButtonHandler(@NotNull String id){
		return Optional.of(buttonHandlers.get(id));
	}
	
	public static Optional<ISelectMenuHandler> getSelectionMenuHandler(@NotNull String id){
		return Optional.of(selectionMenuHandlers.get(id));
	}
}
