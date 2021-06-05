package fr.raksrinana.rsndiscord.button;

import fr.raksrinana.rsndiscord.button.api.IButtonHandler;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.utils.Utilities.getAllAnnotatedWith;

public class ButtonService{
	private static final Map<String, IButtonHandler> handlers;
	static{
		handlers = getAllAnnotatedWith(ButtonHandler.class, clazz -> (IButtonHandler) clazz.getConstructor().newInstance())
				.collect(Collectors.toMap(IButtonHandler::getButtonId, Function.identity()));
	}
	public static Optional<IButtonHandler> getHandler(@NotNull String id){
		return Optional.of(handlers.get(id));
	}
}
