package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ISO8601LocalDateKeyDeserializer extends KeyDeserializer{
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE;
	
	@Override
	public LocalDate deserializeKey(String key, DeserializationContext ctxt) throws IOException{
		return LocalDate.parse(key, FORMATTER);
	}
}
