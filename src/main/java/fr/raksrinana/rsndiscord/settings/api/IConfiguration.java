package fr.raksrinana.rsndiscord.settings.api;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public interface IConfiguration{
	default Optional<Object> getConfiguration(String currentPath, Deque<String> paths){
		if(paths.isEmpty()){
			return Optional.of(this);
		}
		var name = paths.pop();
		var path = "%s.%s".formatted(currentPath, name);
		
		var declaredFields = Arrays.asList(this.getClass().getDeclaredFields());
		var value = declaredFields.stream()
				.filter(f -> Objects.equals(name, f.getName()))
				.findFirst()
				.map(f -> {
					try{
						return f.get(this);
					}
					catch(IllegalAccessException e){
						throw new IllegalArgumentException("Failed to get value of %s".formatted(path), e);
					}
				})
				.orElseThrow(() -> {
					var availableFields = declaredFields.stream()
							.map(Field::getName)
							.map(n -> "%s.%s".formatted(currentPath, n))
							.collect(Collectors.joining(","));
					return new IllegalArgumentException("Unknown configuration %s, fields available: %s".formatted(path, availableFields));
				});
		if(value instanceof IConfiguration configuration){
			return configuration.getConfiguration(path, paths);
		}
		return Optional.empty();
	}
}
