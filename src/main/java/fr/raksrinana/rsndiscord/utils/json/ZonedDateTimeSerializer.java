package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.NonNull;
import java.io.IOException;
import java.time.ZonedDateTime;

public class ZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime>{
	@Override
	public void serialize(@NonNull final ZonedDateTime date, @NonNull final JsonGenerator jsonGenerator, final @NonNull SerializerProvider serializerProvider) throws IOException{
		jsonGenerator.writeString(date.toString());
	}
}
