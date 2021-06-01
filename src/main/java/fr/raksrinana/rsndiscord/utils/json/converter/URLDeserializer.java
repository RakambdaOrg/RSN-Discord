package fr.raksrinana.rsndiscord.utils.json.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import fr.raksrinana.rsndiscord.log.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import static java.util.Objects.nonNull;

public class URLDeserializer extends JsonDeserializer<URL>{
	@Override
	@Nullable
	public URL deserialize(@NotNull JsonParser jsonParser, @NotNull DeserializationContext deserializationContext) throws IOException{
		try{
			var value = jsonParser.getValueAsString();
			if(nonNull(value) && !value.isBlank() && !value.equals(".")){
				return new URL(value);
			}
		}
		catch(MalformedURLException e){
			Log.getLogger().warn("Failed to parse URL: {}", jsonParser.getValueAsString());
		}
		return null;
	}
}
