package fr.raksrinana.rsndiscord.interaction.modal;

import fr.raksrinana.rsndiscord.interaction.modal.api.IModalHandler;
import fr.raksrinana.rsndiscord.interaction.modal.api.ModalHandler;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import static fr.raksrinana.rsndiscord.utils.Utilities.getAllAnnotatedWith;

public class ModalService{
	private static final Map<String, IModalHandler> modalHandlers;
	
	static{
		modalHandlers = getAllAnnotatedWith(ModalHandler.class, clazz -> (IModalHandler) clazz.getConstructor().newInstance())
				.collect(Collectors.toMap(IModalHandler::getComponentId, Function.identity()));
	}
	
	public static Optional<IModalHandler> getModalHandler(@NotNull String id){
		return Optional.of(modalHandlers.get(id));
	}
}
