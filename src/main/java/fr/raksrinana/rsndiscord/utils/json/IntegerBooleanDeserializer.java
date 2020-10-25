package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import fr.raksrinana.rsndiscord.log.Log;
import lombok.NonNull;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Objects;

public class IntegerBooleanDeserializer extends JsonDeserializer<Boolean>{
	@Override
	public Boolean deserialize(@NonNull final JsonParser jsonParser, @NonNull final DeserializationContext deserializationContext) throws IOException{
		try{
			return Objects.equals("1", jsonParser.getValueAsString());
		}
		catch(final MalformedURLException e){
			Log.getLogger(null).warn("Failed to get boolean {}", jsonParser.getValueAsString());
		}
		return null;
	}
}
