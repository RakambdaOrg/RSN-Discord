package fr.rakambda.rsndiscord.spring.util;

import fr.rakambda.rsndiscord.spring.Application;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class GraphQlService{
	private static final Pattern INCLUDE_PATTERN = Pattern.compile("#include \"(.*)\"");
	
	@NonNull
	public String readQuery(@NonNull String definition) throws ParseException{
		var resource = Application.class.getClassLoader().getResource(definition);
		if(Objects.isNull(resource)){
			throw new ParseException("Failed to read query resource " + definition);
		}
		
		return readQuery(resource);
	}
	
	@NonNull
	public String readQuery(@NonNull URL resource) throws ParseException{
		try{
			var stringBuilder = new StringBuilder();
			
			try(var is = resource.openStream();
					var ir = new InputStreamReader(is);
					var br = new BufferedReader(ir)){
				
				String line;
				while(Objects.nonNull(line = br.readLine())){
					var matcher = INCLUDE_PATTERN.matcher(line);
					if(matcher.matches()){
						var path = matcher.group(1);
						var include = resource.toURI().resolve(path).toURL();
						
						stringBuilder.append(readQuery(include));
					}
					else{
						stringBuilder.append(line);
					}
				}
				return stringBuilder.toString();
			}
		}
		catch(Exception e){
			throw new ParseException("Failed to read query file", e);
		}
	}
}
