package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.ZonedDateTime;
import static java.time.ZoneId.systemDefault;
import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

public class ISO8601ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime>{
	@Override
	public ZonedDateTime deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException{
		return ZonedDateTime.parse(jsonParser.getValueAsString(), ISO_DATE_TIME).withZoneSameInstant(systemDefault());
	}
}
