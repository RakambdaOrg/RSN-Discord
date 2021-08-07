package fr.raksrinana.rsndiscord.settings;

import java.util.Optional;

public enum ConfigurationOperation{
	ADD,
	REMOVE,
	RESET,
	SET,
	SHOW;
	
	public static Optional<ConfigurationOperation> fromValue(String value){
		for(var operation : ConfigurationOperation.values()){
			if(operation.name().equalsIgnoreCase(value)){
				return Optional.of(operation);
			}
		}
		return Optional.empty();
	}
}
