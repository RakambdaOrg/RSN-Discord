package fr.rakambda.rsndiscord.spring.json.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import static java.util.Objects.nonNull;

@Slf4j
public class URLDeserializer extends JsonDeserializer<URL>{
	@Override
	@Nullable
	public URL deserialize(@NotNull JsonParser jsonParser, @NotNull DeserializationContext deserializationContext) throws IOException{
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
