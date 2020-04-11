package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ISO8601ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime>{
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
	
	@Override
	public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException{
		return ZonedDateTime.parse(jsonParser.getValueAsString(), FORMATTER).withZoneSameInstant(ZoneId.systemDefault());
	}
}
