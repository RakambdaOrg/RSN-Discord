package fr.raksrinana.rsndiscord.utils.reaction;

import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.utils.SortedList;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class ReactionUtils{
	public static final String DELETE_KEY = "delete";
	public static final String USER_ID_KEY = "user_id";
	public static final String URL_KEY = "url";
	private static final Collection<ReactionHandler> handlers = new SortedList<>();
	
	public static void addHandler(@NonNull ReactionHandler handler){
		handlers.add(handler);
	}
	
	public static void registerAllHandlers(){
		Utilities.getAllInstancesOf(ReactionHandler.class, Main.class.getPackage().getName() + ".utils.reaction", c -> {
			try{
				return c.getConstructor().newInstance();
			}
			catch(InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e){
				Log.getLogger(null).error("Failed to create instance of {}", c.getName(), e);
			}
			return null;
		}).stream().peek(c -> Log.getLogger(null).info("Loaded reaction handler {}", c.getClass().getName())).forEach(ReactionUtils::addHandler);
	}
	
	public static Collection<ReactionHandler> getHandlers(){
		return handlers;
	}
}
