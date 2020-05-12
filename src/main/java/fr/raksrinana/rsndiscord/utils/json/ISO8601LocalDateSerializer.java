package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ISO8601LocalDateSerializer extends JsonSerializer<LocalDate>{
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE;
	
	@Override
	public void serialize(LocalDate date, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException{
		jsonGenerator.writeString(date.format(FORMATTER));
	}
}
