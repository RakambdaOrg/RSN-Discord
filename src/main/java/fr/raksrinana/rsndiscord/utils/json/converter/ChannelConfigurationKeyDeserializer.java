package fr.raksrinana.rsndiscord.utils.json.converter;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChannelConfigurationKeyDeserializer extends KeyDeserializer{
	@Override
	@Nullable
	public Object deserializeKey(@NotNull String key, @NotNull DeserializationContext context){
		return new ChannelConfiguration(Long.parseLong(key));
	}
}
