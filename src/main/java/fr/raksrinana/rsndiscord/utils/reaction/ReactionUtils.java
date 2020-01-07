package fr.raksrinana.rsndiscord.utils.reaction;

import fr.raksrinana.rsndiscord.utils.SortedList;
import lombok.NonNull;
import java.util.Collection;

public class ReactionUtils{
	public static final String DELETE_KEY = "delete";
	public static final String USER_ID_KEY = "user_id";
	public static final String URL_KEY = "url";
	private static final Collection<ReactionHandler> handlers = new SortedList<>();
	
	public static void addHandler(@NonNull ReactionHandler handler){
		handlers.add(handler);
	}
	
	public static Collection<ReactionHandler> getHandlers(){
		return handlers;
	}
}
