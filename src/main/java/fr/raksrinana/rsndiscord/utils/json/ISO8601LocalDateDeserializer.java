package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.time.LocalDate;
import static java.time.format.DateTimeFormatter.ISO_DATE;

public class ISO8601LocalDateDeserializer extends JsonDeserializer<LocalDate>{
	@Override
	@Nullable
	public LocalDate deserialize(@NotNull JsonParser jsonParser, @NotNull DeserializationContext context) throws IOException{
		return LocalDate.parse(jsonParser.getValueAsString(), ISO_DATE);
	}
}
