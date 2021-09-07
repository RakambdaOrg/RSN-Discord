package fr.raksrinana.rsndiscord.utils.json.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.net.URL;

public class URLSerializer extends JsonSerializer<URL>{
	@Override
	public void serialize(@NotNull URL url, @NotNull JsonGenerator jsonGenerator, @NotNull SerializerProvider serializerProvider) throws IOException{
		jsonGenerator.writeString(url.toString());
	}
}
