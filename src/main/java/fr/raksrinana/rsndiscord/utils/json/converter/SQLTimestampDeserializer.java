package fr.raksrinana.rsndiscord.utils.json.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import static java.time.Instant.ofEpochSecond;
import static java.time.ZoneId.systemDefault;

public class SQLTimestampDeserializer extends JsonDeserializer<ZonedDateTime>{
	@Override
	@Nullable
	public ZonedDateTime deserialize(@NotNull JsonParser jsonParser, @NotNull DeserializationContext deserializationContext) throws IOException{
		return ZonedDateTime.ofInstant(ofEpochSecond(jsonParser.getValueAsLong()), ZoneId.of("UTC")).withZoneSameInstant(systemDefault());
	}
}
