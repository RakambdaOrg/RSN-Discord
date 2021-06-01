package fr.raksrinana.rsndiscord.utils.json.converter;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserConfigurationKeyDeserializer extends KeyDeserializer{
	@Override
	@Nullable
	public Object deserializeKey(@NotNull String key, @NotNull DeserializationContext context){
		return new UserConfiguration(Long.parseLong(key));
	}
}
