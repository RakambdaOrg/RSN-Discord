package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.NonNull;
import java.io.IOException;

public class UnknownDeserializer extends JsonDeserializer<Object>{
	@Override
	public Object deserialize(@NonNull final JsonParser jsonParser, @NonNull final DeserializationContext deserializationContext) throws IOException{
		if(jsonParser.isNaN()){
			return null;
		}
		throw new IOException("Unknown field isn't null: " + jsonParser.getText());
	}
}
