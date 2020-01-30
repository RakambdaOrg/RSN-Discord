package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.NonNull;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime>{
	@Override
	public void serialize(@NonNull final LocalDateTime date, @NonNull final JsonGenerator jsonGenerator, final @NonNull SerializerProvider serializerProvider) throws IOException{
		final var utcDate = ZonedDateTime.ofLocal(date, ZoneId.systemDefault(), null).withZoneSameInstant(ZoneId.of("UTC"));
		jsonGenerator.writeNumber(utcDate.toInstant().toEpochMilli());
	}
}
