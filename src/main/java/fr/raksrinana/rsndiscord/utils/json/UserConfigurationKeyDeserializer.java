package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;

public class UserConfigurationKeyDeserializer extends KeyDeserializer{
	@Override
	public Object deserializeKey(String key, DeserializationContext context){
		return new UserConfiguration(Long.parseLong(key));
	}
}
