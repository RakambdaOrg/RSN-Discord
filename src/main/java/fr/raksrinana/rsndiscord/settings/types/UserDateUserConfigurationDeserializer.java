package fr.raksrinana.rsndiscord.settings.types;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import java.io.IOException;

class UserDateUserConfigurationDeserializer extends JsonDeserializer<UserConfiguration>{
	@Override
	public UserConfiguration deserialize(@NonNull final JsonParser jsonParser, @NonNull final DeserializationContext deserializationContext) throws IOException{
		try{
			final JsonNode node = jsonParser.getCodec().readTree(jsonParser);
			if(node.getNodeType() == JsonNodeType.OBJECT){
				return new UserConfiguration(node.get("userId").asLong());
			}
			else{
				return new UserConfiguration(node.asLong());
			}
		}
		catch(final IOException e){
			Log.getLogger(null).warn("Failed get user {}", jsonParser.getValueAsString());
		}
		return null;
	}
}
