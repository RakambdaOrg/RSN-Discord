package fr.mrcraftcod.gunterdiscord.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class SQLTimestampDeserializer extends JsonDeserializer<LocalDateTime>{
	@Override
	public LocalDateTime deserialize(@Nonnull final JsonParser jsonParser, @Nonnull final DeserializationContext deserializationContext) throws IOException{
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(jsonParser.getValueAsLong() * 1000L), ZoneId.of("UTC"));
	}
}
