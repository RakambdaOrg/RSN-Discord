package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fr.raksrinana.rsndiscord.modules.settings.types.ChannelConfiguration;
import lombok.NonNull;
import java.io.IOException;

public class ChannelConfigurationKeySerializer extends JsonSerializer<ChannelConfiguration>{
	@Override
	public void serialize(@NonNull final ChannelConfiguration channelConfiguration, @NonNull final JsonGenerator jsonGenerator, final @NonNull SerializerProvider serializerProvider) throws IOException{
		jsonGenerator.writeFieldName(Long.toString(channelConfiguration.getChannelId()));
	}
}
