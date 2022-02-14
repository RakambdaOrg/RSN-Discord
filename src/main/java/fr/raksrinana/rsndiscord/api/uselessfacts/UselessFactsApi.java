package fr.raksrinana.rsndiscord.api.uselessfacts;

import fr.raksrinana.rsndiscord.api.uselessfacts.data.UselessFact;
import fr.raksrinana.rsndiscord.utils.Utilities;
import kong.unirest.core.GenericType;
import kong.unirest.core.Unirest;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

@Log4j2
public class UselessFactsApi{
	@NotNull
	public static Optional<UselessFact> getFact(){
		log.debug("Requesting random fact");
		var request = Unirest.get("https://uselessfacts.jsph.pl/random.json")
				.queryString("language", "en")
				.asObject(new GenericType<UselessFact>(){});
		
		request.getParsingError().ifPresent(error -> {
			Utilities.reportException("Failed to parse UselessFacts response", error);
			log.warn("Failed to parse UselessFacts response", error);
		});
		if(!request.isSuccess()){
			return empty();
		}
		return ofNullable(request.getBody());
	}
}
