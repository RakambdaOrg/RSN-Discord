package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import java.io.IOException;

public class ChannelConfigurationKeyDeserializer extends KeyDeserializer{
	@Override
	public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException{
		return new ChannelConfiguration(Long.parseLong(key));
	}
}
