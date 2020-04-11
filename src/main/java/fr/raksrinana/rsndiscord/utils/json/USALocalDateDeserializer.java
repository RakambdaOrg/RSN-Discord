package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.NonNull;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class USALocalDateDeserializer extends JsonDeserializer<LocalDate>{
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");
	
	@Override
	public LocalDate deserialize(@NonNull final JsonParser jsonParser, @NonNull final DeserializationContext deserializationContext) throws IOException{
		return LocalDate.parse(jsonParser.getValueAsString(), FORMATTER);
	}
}
