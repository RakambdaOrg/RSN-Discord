package fr.raksrinana.rsndiscord.reaction;

import fr.raksrinana.rsndiscord.reaction.handler.IReactionHandler;
import fr.raksrinana.rsndiscord.reaction.handler.ReactionHandler;
import fr.raksrinana.rsndiscord.utils.SortedList;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import static fr.raksrinana.rsndiscord.utils.Utilities.getAllAnnotatedWith;
@Log4j2
public class ReactionUtils{
	public static final String DELETE_KEY = "delete";
	private static final Collection<IReactionHandler> handlers = new SortedList<>();
	
	public static void registerAllHandlers(){
		log.info("Adding reaction handlers");
		getAllAnnotatedWith(ReactionHandler.class, clazz -> (IReactionHandler) clazz.getConstructor().newInstance())
				.peek(c -> log.info("Loaded reaction handler {}", c.getClass().getName()))
				.forEach(ReactionUtils::addHandler);
	}
	
	public static void addHandler(@NotNull IReactionHandler handler){
		handlers.add(handler);
	}
	
	@NotNull
	public static Collection<IReactionHandler> getHandlers(){
		return handlers;
	}
}
