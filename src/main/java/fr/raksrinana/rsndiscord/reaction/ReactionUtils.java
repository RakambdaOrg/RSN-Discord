package fr.raksrinana.rsndiscord.reaction;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.reaction.handler.IReactionHandler;
import fr.raksrinana.rsndiscord.reaction.handler.ReactionHandler;
import fr.raksrinana.rsndiscord.utils.SortedList;
import org.jetbrains.annotations.NotNull;
import java.util.Collection;
import static fr.raksrinana.rsndiscord.utils.Utilities.getAllAnnotatedWith;

public class ReactionUtils{
	public static final String DELETE_KEY = "delete";
	private static final Collection<IReactionHandler> handlers = new SortedList<>();
	
	public static void registerAllHandlers(){
		getAllAnnotatedWith(ReactionHandler.class, clazz -> (IReactionHandler) clazz.getConstructor().newInstance())
				.peek(c -> Log.getLogger(null).info("Loaded reaction handler {}", c.getClass().getName()))
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
