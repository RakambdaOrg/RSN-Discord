package fr.raksrinana.rsndiscord.utils.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonFactoryBuilder;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import kong.unirest.GenericType;
import kong.unirest.ObjectMapper;
import java.io.IOException;
import java.lang.reflect.Type;

public class JacksonObjectMapper implements ObjectMapper{
	private final com.fasterxml.jackson.databind.ObjectMapper mapper;
	
	public JacksonObjectMapper(){
		var factoryBuilder = new JsonFactoryBuilder();
		factoryBuilder.enable(JsonReadFeature.ALLOW_TRAILING_COMMA);
		mapper = new com.fasterxml.jackson.databind.ObjectMapper(factoryBuilder.build());
		mapper.setVisibility(mapper.getSerializationConfig()
				.getDefaultVisibilityChecker()
				.withFieldVisibility(JsonAutoDetect.Visibility.ANY)
				.withGetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withSetterVisibility(JsonAutoDetect.Visibility.NONE)
				.withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
		mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
		mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);
	}
	
	@Override
	public <T> T readValue(String value, Class<T> valueType){
		try{
			return mapper.readValue(value, valueType);
		}
		catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public <T> T readValue(String value, GenericType<T> genericType){
		try{
			return mapper.readValue(value, new TypeReference<>(){
				@Override
				public Type getType(){
					return genericType.getType();
				}
			});
		}
		catch(IOException var4){
			throw new RuntimeException(var4);
		}
	}
	
	@Override
	public String writeValue(Object value){
		try{
			return mapper.writeValueAsString(value);
		}
		catch(JsonProcessingException e){
			throw new RuntimeException(e);
		}
	}
}
