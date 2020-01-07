package fr.raksrinana.rsndiscord.settings;

import fr.raksrinana.rsndiscord.utils.log.Log;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public interface CompositeConfiguration{
	default void cleanFields() throws IllegalAccessException{
		for(final var field : this.getClass().getFields()){
			final var fieldValue = field.get(this);
			if(cleanObject(fieldValue)){
				field.set(this, null);
			}
		}
	}
	
	default boolean cleanObject(Object fieldValue) throws IllegalAccessException{
		if(fieldValue instanceof AtomicConfiguration){
			return ((AtomicConfiguration) fieldValue).shouldBeRemoved();
		}
		else if(fieldValue instanceof CompositeConfiguration){
			((CompositeConfiguration) fieldValue).cleanFields();
			return false;
		}
		else if(fieldValue instanceof Collection){
			final var collection = (Collection<?>) fieldValue;
			collection.removeIf(elem -> elem instanceof AtomicConfiguration && ((AtomicConfiguration) elem).shouldBeRemoved());
			collection.forEach(elem -> {
				try{
					cleanObject(elem);
				}
				catch(IllegalAccessException e){
					Log.getLogger(null).error("Failed to clean settings object {}", this.getClass(), e);
				}
			});
			return false;
		}
		else if(fieldValue instanceof Map){
			final var map = (Map<?, ?>) fieldValue;
			final var keysToRemove = map.entrySet().stream().filter(entry -> entry.getValue() instanceof AtomicConfiguration && ((AtomicConfiguration) entry.getValue()).shouldBeRemoved()).map(Map.Entry::getKey).collect(Collectors.toSet());
			keysToRemove.forEach(map::remove);
			map.values().forEach(elem -> {
				try{
					cleanObject(elem);
				}
				catch(IllegalAccessException e){
					Log.getLogger(null).error("Failed to clean settings object {}", this.getClass(), e);
				}
			});
			return false;
		}
		return false;
	}
}
