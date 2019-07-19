package fr.mrcraftcod.gunterdiscord.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class URLDeserializer extends JsonDeserializer<URL>{
	private static final Logger LOGGER = LoggerFactory.getLogger(URLDeserializer.class);
	
	@Override
	public URL deserialize(@Nonnull final JsonParser jsonParser, @Nonnull final DeserializationContext deserializationContext) throws IOException{
		try{
			return new URL(jsonParser.getValueAsString());
		}
		catch(MalformedURLException e){
			LOGGER.warn("Failed to parse URL {}", jsonParser.getValueAsString());
		}
		return null;
	}
}


