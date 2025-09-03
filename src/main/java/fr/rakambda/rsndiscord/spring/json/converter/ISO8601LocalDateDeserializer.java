package fr.rakambda.rsndiscord.spring.json.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.io.IOException;
import java.time.LocalDate;
import static java.time.format.DateTimeFormatter.ISO_DATE;

public class ISO8601LocalDateDeserializer extends JsonDeserializer<LocalDate>{
	@Override
	@Nullable
	public LocalDate deserialize(@NonNull JsonParser jsonParser, @NonNull DeserializationContext context) throws IOException{
		return LocalDate.parse(jsonParser.getValueAsString(), ISO_DATE);
	}
}
