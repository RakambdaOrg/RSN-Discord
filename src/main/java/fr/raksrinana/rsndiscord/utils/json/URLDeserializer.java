package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class URLDeserializer extends JsonDeserializer<URL>{
	@Override
	public URL deserialize(@NonNull final JsonParser jsonParser, @NonNull final DeserializationContext deserializationContext) throws IOException{
		try{
			final var value = jsonParser.getValueAsString();
			if(Objects.nonNull(value) && !value.isBlank()){
				return new URL(value);
			}
		}
		catch(final MalformedURLException e){
			Log.getLogger(null).warn("Failed to parse URL: {}", jsonParser.getValueAsString());
		}
		return null;
	}
}
