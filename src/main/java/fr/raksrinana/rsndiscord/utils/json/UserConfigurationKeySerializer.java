package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fr.raksrinana.rsndiscord.modules.settings.types.UserConfiguration;
import lombok.NonNull;
import java.io.IOException;

public class UserConfigurationKeySerializer extends JsonSerializer<UserConfiguration>{
	@Override
	public void serialize(@NonNull final UserConfiguration userConfiguration, @NonNull final JsonGenerator jsonGenerator, final @NonNull SerializerProvider serializerProvider) throws IOException{
		jsonGenerator.writeFieldName(Long.toString(userConfiguration.getUserId()));
	}
}
