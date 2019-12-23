package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ISO8601DateDeserializer extends JsonDeserializer<LocalDate>{
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE;
	
	@Override
	public LocalDate deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException{
		return LocalDate.parse(jsonParser.getValueAsString(), FORMATTER);
	}
}
