package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import java.time.LocalDate;
import static java.time.format.DateTimeFormatter.ISO_DATE;

public class ISO8601LocalDateKeyDeserializer extends KeyDeserializer{
	@Override
	public LocalDate deserializeKey(String key, DeserializationContext ctxt){
		return LocalDate.parse(key, ISO_DATE);
	}
}
