package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import static java.time.Instant.ofEpochMilli;
import static java.time.ZoneId.systemDefault;

public class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime>{
	@Override
	@Nullable
	public ZonedDateTime deserialize(@NotNull JsonParser jsonParser, @NotNull DeserializationContext deserializationContext) throws IOException{
		long timestamp = jsonParser.getValueAsLong(-1L);
		if(timestamp >= 0){
			return ZonedDateTime.ofInstant(ofEpochMilli(timestamp), ZoneId.of("UTC")).withZoneSameInstant(systemDefault());
		}
		return ZonedDateTime.parse(jsonParser.getValueAsString());
	}
}
