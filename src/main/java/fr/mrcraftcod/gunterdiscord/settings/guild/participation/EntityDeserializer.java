package fr.mrcraftcod.gunterdiscord.settings.guild.participation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import fr.mrcraftcod.gunterdiscord.utils.json.HexColorDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class EntityDeserializer extends JsonDeserializer<Set<EntityScore>>{
	private static final Logger LOGGER = LoggerFactory.getLogger(HexColorDeserializer.class);
	
	@Override
	public Set<EntityScore> deserialize(@Nonnull final JsonParser jsonParser, @Nonnull final DeserializationContext deserializationContext) throws IOException{
		try{
			final var set = new HashSet<EntityScore>();
			JsonNode node = jsonParser.getCodec().readTree(jsonParser);
			if(node.getNodeType().equals(JsonNodeType.ARRAY)){
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
		catch(IOException e){
			LOGGER.warn("Failed get entity score type {}", jsonParser.getValueAsString());
		}
		return null;
	}
}
