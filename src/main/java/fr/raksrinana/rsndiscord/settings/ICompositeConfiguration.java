package fr.raksrinana.rsndiscord.settings;

import fr.raksrinana.rsndiscord.log.Log;
import net.dv8tion.jda.api.entities.Guild;
import org.apache.commons.lang3.reflect.FieldUtils;
import java.util.*;
import java.util.stream.Collectors;

public interface ICompositeConfiguration{
	default void cleanFields(Guild guild, String fieldName) throws Exception{
		for(var field : this.getClass().getDeclaredFields()){
			var fieldValue = FieldUtils.readField(field, this, true);
			if(cleanObject(guild, fieldValue, fieldName + "." + field.getName())){
				Log.getLogger(guild).debug("Setting field {}.{} to null", fieldName, field);
				field.set(this, null);
			}
		}
	}
	
	default boolean atomicShouldBeRemoved(Object object){
		return object instanceof IAtomicConfiguration && ((IAtomicConfiguration) object).shouldBeRemoved();
	}
	
	default boolean cleanObject(Guild guild, Object fieldValue, String fieldName) throws Exception{
		if(fieldValue instanceof IAtomicConfiguration){
			return ((IAtomicConfiguration) fieldValue).shouldBeRemoved();
		}
		else if(fieldValue instanceof ICompositeConfiguration){
			((ICompositeConfiguration) fieldValue).cleanFields(guild, fieldName);
			return false;
		}
		else if(fieldValue instanceof Collection){
			try{
				var collection = (Collection<?>) fieldValue;
				var toRemove = List.copyOf(collection).stream()
						.filter(this::atomicShouldBeRemoved)
						.collect(Collectors.toList());
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
			}
			catch(Exception e){
				throw new RuntimeException("Failed to clean field " + fieldName, e);
			}
			return false;
		}
		else if(fieldValue instanceof Map){
			var map = (Map<?, ?>) fieldValue;
			var toRemove = new HashMap<>(map).entrySet().stream()
					.filter(entry -> Objects.isNull(entry.getValue())
							|| atomicShouldBeRemoved(entry.getKey())
							|| atomicShouldBeRemoved(entry.getValue()))
					.map(Map.Entry::getKey)
					.collect(Collectors.toSet());
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
