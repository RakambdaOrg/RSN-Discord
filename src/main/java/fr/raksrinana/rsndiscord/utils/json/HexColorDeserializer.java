package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;

public class HexColorDeserializer extends JsonDeserializer<Color>{
	private static final Logger LOGGER = LoggerFactory.getLogger(HexColorDeserializer.class);
	
	@Override
	public Color deserialize(@NonNull final JsonParser jsonParser, @NonNull final DeserializationContext deserializationContext) throws IOException{
		try{
			return Color.decode("#" + jsonParser.getValueAsString());
		}
		catch(final MalformedURLException e){
			LOGGER.warn("Failed to get color {}", jsonParser.getValueAsString());
		}
		return null;
	}
}
