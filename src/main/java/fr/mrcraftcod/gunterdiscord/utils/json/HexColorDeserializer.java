package fr.mrcraftcod.gunterdiscord.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Nonnull;
import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;

public class HexColorDeserializer extends JsonDeserializer<Color>{
	private static final Logger LOGGER = LoggerFactory.getLogger(HexColorDeserializer.class);
	
	@Override
	public Color deserialize(@Nonnull final JsonParser jsonParser, @Nonnull final DeserializationContext deserializationContext) throws IOException{
		try{
			return Color.decode("#" + jsonParser.getValueAsString());
		}
		catch(MalformedURLException e){
			LOGGER.warn("Failed to get color {}", jsonParser.getValueAsString());
		}
		return null;
	}
}


