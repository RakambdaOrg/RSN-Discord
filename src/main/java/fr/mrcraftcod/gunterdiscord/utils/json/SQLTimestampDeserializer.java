package fr.mrcraftcod.gunterdiscord.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.Date;

public class SQLTimestampDeserializer extends JsonDeserializer<Date>{
	@Override
	public Date deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException{
		return new Date(Long.parseLong(jsonParser.getText()) * 1000L);
	}
}
