package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;

public class ChannelConfigurationKeyDeserializer extends KeyDeserializer{
	@Override
	public Object deserializeKey(String key, DeserializationContext context){
		return new ChannelConfiguration(Long.parseLong(key));
	}
}
