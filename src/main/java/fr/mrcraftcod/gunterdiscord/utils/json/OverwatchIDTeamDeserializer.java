package fr.mrcraftcod.gunterdiscord.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import javax.annotation.Nonnull;
import java.io.IOException;

public class OverwatchIDTeamDeserializer extends JsonDeserializer<Integer>{
	@Override
	public Integer deserialize(@Nonnull final JsonParser jsonParser, @Nonnull final DeserializationContext deserializationContext) throws IOException{
		final JsonNode weekNode = jsonParser.getCodec().readTree(jsonParser);
		return weekNode.get("id").intValue();
	}
}


