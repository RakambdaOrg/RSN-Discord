package fr.raksrinana.rsndiscord.settings;

import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.commons.lang3.reflect.FieldUtils;
import java.util.*;
import java.util.stream.Collectors;

public interface CompositeConfiguration{
	default void cleanFields(@NonNull Guild guild, String fieldName) throws Exception{
		for(final var field : this.getClass().getDeclaredFields()){
			final var fieldValue = FieldUtils.readField(field, this, true);
			if(cleanObject(guild, fieldValue, fieldName + "." + field.getName())){
				Log.getLogger(guild).debug("Setting field {}.{} to null", fieldName, field);
				field.set(this, null);
			}
		}
	}
	
	default boolean atomicShouldBeRemoved(Object object){
		return object instanceof AtomicConfiguration && ((AtomicConfiguration) object).shouldBeRemoved();
	}
	
	default boolean cleanObject(@NonNull Guild guild, Object fieldValue, String fieldName) throws Exception{
		if(fieldValue instanceof AtomicConfiguration){
			return ((AtomicConfiguration) fieldValue).shouldBeRemoved();
		}
		else if(fieldValue instanceof CompositeConfiguration){
			((CompositeConfiguration) fieldValue).cleanFields(guild, fieldName);
			return false;
		}
		else if(fieldValue instanceof Collection){
			try{
				final var collection = (Collection<?>) fieldValue;
				final var toRemove = List.copyOf(collection).stream().filter(this::atomicShouldBeRemoved).collect(Collectors.toList());
				if(!toRemove.isEmpty()){
					Log.getLogger(guild).debug("Removing values {} from collection {}", toRemove, fieldName);
				}
				collection.removeAll(toRemove);
				collection.forEach(elem -> {
					try{
						cleanObject(guild, elem, fieldName);
					}
					catch(Exception e){
						Log.getLogger(guild).error("Failed to clean settings object {}", this.getClass(), e);
					}
				});
			}catch(Exception e){
				throw new RuntimeException("Failed to clean field " + fieldName, e);
			}
			return false;
		}
		else if(fieldValue instanceof Map){
			final var map = (Map<?, ?>) fieldValue;
			final var toRemove = new HashMap<>(map).entrySet().stream().filter(entry -> Objects.isNull(entry.getValue()) || atomicShouldBeRemoved(entry.getKey()) || atomicShouldBeRemoved(entry.getValue())).map(Map.Entry::getKey).collect(Collectors.toSet());
			if(!toRemove.isEmpty()){
				Log.getLogger(guild).debug("Removing keys {} from map {}", toRemove, fieldName);
			}
			toRemove.forEach(map::remove);
			map.values().forEach(elem -> {
				try{
					cleanObject(guild, elem, fieldName);
				}
				catch(Exception e){
					Log.getLogger(guild).error("Failed to clean settings object {}", this.getClass(), e);
				}
			});
			return false;
		}
		return false;
	}
}
