package fr.mrcraftcod.gunterdiscord.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class USADateStringDeserializer extends JsonDeserializer<LocalDate>{
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");
	
	@Override
	public LocalDate deserialize(@Nonnull final JsonParser jsonParser, @Nonnull final DeserializationContext deserializationContext) throws IOException{
		return LocalDate.parse(jsonParser.getValueAsString(), FORMATTER);
	}
}


