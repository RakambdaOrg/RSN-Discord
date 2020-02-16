package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import fr.raksrinana.rsndiscord.utils.log.Log;
import lombok.NonNull;
import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;

public class HexColorDeserializer extends JsonDeserializer<Color>{
	@Override
	public Color deserialize(@NonNull final JsonParser jsonParser, @NonNull final DeserializationContext deserializationContext) throws IOException{
		try{
			var str = jsonParser.getValueAsString();
			if(str != null && !str.isBlank()){
				if(!str.startsWith("#")){
					str = "#" + str;
				}
				return Color.decode(str);
			}
		}
		catch(final MalformedURLException e){
			Log.getLogger(null).warn("Failed to get color {}", jsonParser.getValueAsString());
		}
		return null;
	}
}
