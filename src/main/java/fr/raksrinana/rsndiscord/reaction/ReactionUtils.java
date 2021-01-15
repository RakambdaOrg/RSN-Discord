package fr.raksrinana.rsndiscord.reaction;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.reaction.handler.IReactionHandler;
import fr.raksrinana.rsndiscord.reaction.handler.ReactionHandler;
import fr.raksrinana.rsndiscord.utils.SortedList;
import lombok.NonNull;
import java.util.Collection;
import static fr.raksrinana.rsndiscord.utils.Utilities.getAllAnnotatedWith;

public class ReactionUtils{
	public static final String DELETE_KEY = "delete";
	public static final String USER_ID_KEY = "user_id";
	public static final String URL_KEY = "url";
	private static final Collection<IReactionHandler> handlers = new SortedList<>();
	
	public static void registerAllHandlers(){
		getAllAnnotatedWith(ReactionHandler.class, clazz -> (IReactionHandler) clazz.getConstructor().newInstance())
				.peek(c -> Log.getLogger(null).info("Loaded reaction handler {}", c.getClass().getName()))
				.forEach(ReactionUtils::addHandler);
	}
	
	public static void addHandler(@NonNull IReactionHandler handler){
		handlers.add(handler);
	}
	
	public static Collection<IReactionHandler> getHandlers(){
		return handlers;
	}
}
