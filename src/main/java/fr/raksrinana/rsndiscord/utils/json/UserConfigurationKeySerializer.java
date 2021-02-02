package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;

public class UserConfigurationKeySerializer extends JsonSerializer<UserConfiguration>{
	@Override
	public void serialize(@NotNull UserConfiguration userConfiguration, @NotNull JsonGenerator jsonGenerator, @NotNull SerializerProvider serializerProvider) throws IOException{
		jsonGenerator.writeFieldName(Long.toString(userConfiguration.getUserId()));
	}
}
