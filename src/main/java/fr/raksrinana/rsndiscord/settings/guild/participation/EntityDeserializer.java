package fr.raksrinana.rsndiscord.settings.guild.participation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

class EntityDeserializer extends JsonDeserializer<Set<EntityScore>>{
	@Override
	public Set<EntityScore> deserialize(@NonNull final JsonParser jsonParser, @NonNull final DeserializationContext deserializationContext) throws IOException{
		try{
			final var set = new HashSet<EntityScore>();
			final JsonNode node = jsonParser.getCodec().readTree(jsonParser);
			if(node.getNodeType() == JsonNodeType.ARRAY){
				final var it = node.elements();
				while(it.hasNext()){
					final var next = it.next();
					set.add(new EntityScore(next.get("id").asLong(), next.has("name") ? next.get("name").asText() : null, next.get("score").asLong()));
				}
			}
			else{
				final var it = node.fields();
				while(it.hasNext()){
					final var next = it.next();
					final var entity = new EntityScore(Long.parseLong(next.getKey()));
					entity.setScore(next.getValue().asLong());
					set.add(entity);
				}
			}
			return set;
		}
		catch(final IOException e){
			Log.getLogger(null).warn("Failed get entity score type {}", jsonParser.getValueAsString());
		}
		return null;
	}
}
