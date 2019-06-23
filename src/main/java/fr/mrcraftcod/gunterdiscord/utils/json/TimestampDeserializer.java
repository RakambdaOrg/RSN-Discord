package fr.mrcraftcod.gunterdiscord.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Date;

public class TimestampDeserializer extends JsonDeserializer<Date>{
	@Override
	public Date deserialize(@Nonnull final JsonParser jsonParser, @Nonnull final DeserializationContext deserializationContext) throws IOException{
		return new Date(jsonParser.getValueAsLong());
	}
}
