package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.time.Duration;

public class DurationSerializer extends JsonSerializer<Duration>{
	@Override
	public void serialize(@NotNull Duration duration, @NotNull JsonGenerator jsonGenerator, @NotNull SerializerProvider serializerProvider) throws IOException{
		jsonGenerator.writeNumber(duration.toMillis());
	}
}
