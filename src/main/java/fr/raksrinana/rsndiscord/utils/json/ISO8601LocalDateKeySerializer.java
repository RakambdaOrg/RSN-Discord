package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.time.LocalDate;
import static java.time.format.DateTimeFormatter.ISO_DATE;

public class ISO8601LocalDateKeySerializer extends JsonSerializer<LocalDate>{
	@Override
	public void serialize(@NotNull LocalDate date, @NotNull JsonGenerator jsonGenerator, @NotNull SerializerProvider serializers) throws IOException{
		jsonGenerator.writeFieldName(date.format(ISO_DATE));
	}
}
