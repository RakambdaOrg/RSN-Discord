package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import fr.raksrinana.rsndiscord.utils.overwatch.year2020.content.week.event.match.Match;
import lombok.NonNull;
import java.io.IOException;

public class MatchDeserializer extends JsonDeserializer<Match>{
	@Override
	public Match deserialize(@NonNull final JsonParser jsonParser, @NonNull final DeserializationContext deserializationContext) throws IOException{
		if(!jsonParser.isNaN() && !jsonParser.getText().isBlank() && !jsonParser.getText().equals("false")){
			return deserializationContext.readValue(jsonParser, Match.class);
		}
		return null;
	}
}
