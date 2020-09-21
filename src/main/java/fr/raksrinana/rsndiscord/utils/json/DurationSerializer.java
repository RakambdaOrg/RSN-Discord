package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.NonNull;
import java.io.IOException;
import java.time.Duration;

public class DurationSerializer extends JsonSerializer<Duration>{
	@Override
	public void serialize(@NonNull final Duration duration, @NonNull final JsonGenerator jsonGenerator, final @NonNull SerializerProvider serializerProvider) throws IOException{
		jsonGenerator.writeNumber(duration.toMillis());
	}
}
