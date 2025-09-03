package fr.rakambda.rsndiscord.spring.json.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import static java.util.Objects.nonNull;

@Log4j2
public class URLDeserializer extends JsonDeserializer<URL>{
	@Override
	@Nullable
	public URL deserialize(@NonNull JsonParser jsonParser, @NonNull DeserializationContext deserializationContext) throws IOException{
		try{
			var value = jsonParser.getValueAsString();
			if(nonNull(value) && !value.isBlank() && !value.equals(".")){
				return URI.create(value).toURL();
			}
		}
		catch(MalformedURLException e){
			log.warn("Failed to parse URL: {}", jsonParser.getValueAsString());
		}
		return null;
	}
}
