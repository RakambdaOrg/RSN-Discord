package fr.raksrinana.rsndiscord.settings.api;

import net.dv8tion.jda.api.entities.Guild;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.LogManager;
import java.util.*;
import java.util.stream.Collectors;

public interface ICompositeConfiguration{
	default void cleanFields(Guild guild, String fieldName) throws Exception{
		for(var field : getClass().getDeclaredFields()){
			var fieldValue = FieldUtils.readField(field, this, true);
			if(cleanObject(guild, fieldValue, fieldName + "." + field.getName())){
				LogManager.getLogger(ICompositeConfiguration.class).debug("Setting field {}.{} to null", fieldName, field);
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
					LogManager.getLogger(ICompositeConfiguration.class).debug("Removing values {} from collection {}", toRemove, fieldName);
				}
				collection.removeAll(toRemove);
				collection.forEach(elem -> {
					try{
						cleanObject(guild, elem, fieldName);
					}
					catch(Exception e){
						LogManager.getLogger(ICompositeConfiguration.class).error("Failed to clean settings object {}", getClass(), e);
					}
				});
			}
			catch(Exception e){
				throw new RuntimeException("Failed to clean field " + fieldName, e);
			}
			return false;
		}
		else if(fieldValue instanceof Map<?, ?> map){
			var toRemove = new HashMap<>(map).entrySet().stream()
					.filter(entry -> Objects.isNull(entry.getValue())
							|| atomicShouldBeRemoved(entry.getKey())
							|| atomicShouldBeRemoved(entry.getValue()))
					.map(Map.Entry::getKey)
					.collect(Collectors.toSet());
			if(!toRemove.isEmpty()){
				LogManager.getLogger(ICompositeConfiguration.class).debug("Removing keys {} from map {}", toRemove, fieldName);
			}
			toRemove.forEach(map::remove);
			map.values().forEach(elem -> {
				try{
					cleanObject(guild, elem, fieldName);
				}
				catch(Exception e){
					LogManager.getLogger(ICompositeConfiguration.class).error("Failed to clean settings object {}", getClass(), e);
				}
			});
			return false;
		}
		return false;
	}
	
	
}
