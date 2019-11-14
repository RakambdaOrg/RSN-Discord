package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.NonNull;
import java.io.IOException;

public class OverwatchIDSeriesDeserializer extends JsonDeserializer<Integer>{
	@Override
	public Integer deserialize(@NonNull final JsonParser jsonParser, @NonNull final DeserializationContext deserializationContext) throws IOException{
		final JsonNode weekNode = jsonParser.getCodec().readTree(jsonParser);
		return weekNode.get("id").intValue();
	}
}
