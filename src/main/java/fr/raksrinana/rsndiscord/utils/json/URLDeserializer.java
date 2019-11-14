package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class URLDeserializer extends JsonDeserializer<URL>{
	private static final Logger LOGGER = LoggerFactory.getLogger(URLDeserializer.class);
	
	@Override
	public URL deserialize(@NonNull final JsonParser jsonParser, @NonNull final DeserializationContext deserializationContext) throws IOException{
		try{
			return new URL(jsonParser.getValueAsString());
		}
		catch(final MalformedURLException e){
			LOGGER.trace("Failed to parse URL {}", jsonParser.getValueAsString());
		}
		return null;
	}
}
