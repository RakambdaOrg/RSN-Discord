package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.raksrinana.rsndiscord.utils.eslgaming.model.matchday.match.MatchMeta;
import lombok.NonNull;
import java.io.IOException;
import java.util.List;

public class MatchMetaListDeserializer extends JsonDeserializer<List<MatchMeta>>{
	@Override
	public List<MatchMeta> deserialize(@NonNull final JsonParser jsonParser, @NonNull final DeserializationContext deserializationContext) throws IOException{
		if(!jsonParser.isNaN() && !jsonParser.getText().isBlank()){
			return ((ObjectMapper) jsonParser.getCodec()).readValue(jsonParser.getText(), new TypeReference<>(){});
		}
		return null;
	}
}
