package fr.raksrinana.rsndiscord.utils.json.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.time.Duration;
import static java.time.Duration.ofMillis;

public class DurationDeserializer extends JsonDeserializer<Duration>{
	@Override
	@Nullable
	public Duration deserialize(@NotNull JsonParser jsonParser, @NotNull DeserializationContext deserializationContext) throws IOException{
		var value = jsonParser.getValueAsLong();
		if(value > 0){
			return ofMillis(value);
		}
		return null;
	}
}
