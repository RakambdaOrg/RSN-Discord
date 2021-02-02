package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;

public class ChannelConfigurationKeySerializer extends JsonSerializer<ChannelConfiguration>{
	@Override
	public void serialize(@NotNull ChannelConfiguration channelConfiguration, @NotNull JsonGenerator jsonGenerator, @NotNull SerializerProvider serializerProvider) throws IOException{
		jsonGenerator.writeFieldName(Long.toString(channelConfiguration.getChannelId()));
	}
}
