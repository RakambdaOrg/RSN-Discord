package fr.raksrinana.rsndiscord.utils.json.converter;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import org.jetbrains.annotations.NotNull;
import java.time.LocalDate;
import static java.time.format.DateTimeFormatter.ISO_DATE;

public class ISO8601LocalDateKeyDeserializer extends KeyDeserializer{
	@Override
	public LocalDate deserializeKey(@NotNull String key, @NotNull DeserializationContext ctxt){
		return LocalDate.parse(key, ISO_DATE);
	}
}
