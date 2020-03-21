package fr.raksrinana.rsndiscord.settings;

import fr.raksrinana.rsndiscord.utils.log.Log;
import org.apache.commons.lang3.reflect.FieldUtils;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public interface CompositeConfiguration{
	default void cleanFields(String fieldName) throws Exception{
		for(final var field : this.getClass().getDeclaredFields()){
			final var fieldValue = FieldUtils.readField(field, this, true);
			if(cleanObject(fieldValue, fieldName + "." + field.getName())){
				Log.getLogger(null).debug("Setting field {}.{} to null", fieldName, field);
				field.set(this, null);
			}
		}
	}
	
	default boolean atomicShouldBeRemoved(Object object){
		return object instanceof AtomicConfiguration && ((AtomicConfiguration) object).shouldBeRemoved();
	}
	
	default boolean cleanObject(Object fieldValue, String fieldName) throws Exception{
		if(fieldValue instanceof AtomicConfiguration){
			return ((AtomicConfiguration) fieldValue).shouldBeRemoved();
		}
		else if(fieldValue instanceof CompositeConfiguration){
			((CompositeConfiguration) fieldValue).cleanFields(fieldName);
			return false;
		}
		else if(fieldValue instanceof Collection){
			final var collection = (Collection<?>) fieldValue;
			final var toRemove = collection.stream().filter(this::atomicShouldBeRemoved).collect(Collectors.toList());
			if(!toRemove.isEmpty()){
				Log.getLogger(null).debug("Removing values {} from collection {}", toRemove, fieldName);
			}
			collection.removeAll(toRemove);
			collection.forEach(elem -> {
				try{
					cleanObject(elem, fieldName);
				}
				catch(Exception e){
					Log.getLogger(null).error("Failed to clean settings object {}", this.getClass(), e);
				}
			});
			return false;
		}
		else if(fieldValue instanceof Map){
			final var map = (Map<?, ?>) fieldValue;
			final var toRemove = map.entrySet().stream().filter(entry -> atomicShouldBeRemoved(entry.getKey()) || atomicShouldBeRemoved(entry.getValue())).map(Map.Entry::getKey).collect(Collectors.toSet());
			if(!toRemove.isEmpty()){
				Log.getLogger(null).debug("Removing keys {} from map {}", toRemove, fieldName);
			}
			toRemove.forEach(map::remove);
			map.values().forEach(elem -> {
				try{
					cleanObject(elem, fieldName);
				}
				catch(Exception e){
					Log.getLogger(null).error("Failed to clean settings object {}", this.getClass(), e);
				}
			});
			return false;
		}
		return false;
	}
}
