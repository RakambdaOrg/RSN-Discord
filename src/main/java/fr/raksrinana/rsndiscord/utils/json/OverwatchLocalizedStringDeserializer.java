package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.NonNull;
import java.io.IOException;

public class OverwatchLocalizedStringDeserializer extends JsonDeserializer<String>{
	@Override
	public String deserialize(@NonNull final JsonParser jsonParser, @NonNull final DeserializationContext deserializationContext) throws IOException{
		final JsonNode weekNode = jsonParser.getCodec().readTree(jsonParser);
		return weekNode.get("en_US").textValue();
	}
}
