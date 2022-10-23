package fr.rakambda.rsndiscord.spring.json;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.annotation.Nulls.AS_EMPTY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.CREATOR;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static com.fasterxml.jackson.annotation.PropertyAccessor.GETTER;
import static com.fasterxml.jackson.annotation.PropertyAccessor.SETTER;
import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS;
import static com.fasterxml.jackson.core.json.JsonReadFeature.ALLOW_TRAILING_COMMA;
import static com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS;

@Configuration
public class JacksonConfiguration{
	
	@Bean
	public static ObjectMapper getJsonMapper(){
		return JsonMapper.builder()
				.enable(ALLOW_TRAILING_COMMA)
				.enable(ACCEPT_CASE_INSENSITIVE_ENUMS)
				.enable(ALLOW_COMMENTS)
				.visibility(FIELD, ANY)
				.visibility(GETTER, NONE)
				.visibility(SETTER, NONE)
				.visibility(CREATOR, NONE)
				.serializationInclusion(NON_NULL)
				.withConfigOverride(List.class, o -> o.setSetterInfo(JsonSetter.Value.forValueNulls(AS_EMPTY)))
				.build();
	}
}
