package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.NonNull;
import java.io.IOException;
import java.time.Duration;

public class DurationDeserializer extends JsonDeserializer<Duration>{
	@Override
	public Duration deserialize(@NonNull final JsonParser jsonParser, @NonNull final DeserializationContext deserializationContext) throws IOException{
		final var value = jsonParser.getValueAsLong();
		if(value > 0){
			return Duration.ofMillis(value);
		}
		return null;
	}
}
