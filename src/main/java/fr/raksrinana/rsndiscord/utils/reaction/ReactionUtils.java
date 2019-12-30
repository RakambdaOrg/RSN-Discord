package fr.raksrinana.rsndiscord.utils.reaction;

import lombok.NonNull;
import java.util.Collection;
import java.util.PriorityQueue;

public class ReactionUtils{
	private static final Collection<ReactionHandler> handlers = new PriorityQueue<>();
	
	public static void addHandler(@NonNull ReactionHandler handler){
		handlers.add(handler);
	}
	
	public static Collection<ReactionHandler> getHandlers(){
		return handlers;
	}
}
