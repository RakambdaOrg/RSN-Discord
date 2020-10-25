package fr.raksrinana.rsndiscord.modules.reaction;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.reaction.handler.IReactionHandler;
import fr.raksrinana.rsndiscord.modules.reaction.handler.ReactionHandler;
import fr.raksrinana.rsndiscord.utils.SortedList;
import fr.raksrinana.rsndiscord.utils.Utilities;
import lombok.NonNull;
import java.util.Collection;

public class ReactionUtils{
	public static final String DELETE_KEY = "delete";
	public static final String USER_ID_KEY = "user_id";
	public static final String URL_KEY = "url";
	private static final Collection<IReactionHandler> handlers = new SortedList<>();
	
	public static void registerAllHandlers(){
		Utilities.getAllAnnotatedWith(ReactionHandler.class, clazz -> (IReactionHandler) clazz.getConstructor().newInstance())
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
