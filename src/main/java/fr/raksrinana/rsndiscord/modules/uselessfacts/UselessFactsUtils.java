package fr.raksrinana.rsndiscord.modules.uselessfacts;

import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.modules.uselessfacts.data.UselessFact;
import fr.raksrinana.rsndiscord.utils.Utilities;
import kong.unirest.GenericType;
import kong.unirest.Unirest;
import java.util.Optional;
import static java.util.Optional.ofNullable;

public class UselessFactsUtils{
	public static Optional<UselessFact> getFact(){
		Log.getLogger(null).debug("Requesting random fact");
		var request = Unirest.get("https://uselessfacts.jsph.pl/random.json")
				.queryString("language", "en")
				.asObject(new GenericType<UselessFact>(){});
		request.getParsingError().ifPresent(error -> {
			Utilities.reportException("Failed to parse UselessFacts response", error);
			Log.getLogger(null).warn("Failed to parse UselessFacts response", error);
		});
		if(request.getStatus() == 200){
			return ofNullable(request.getBody());
		}
		return Optional.empty();
	}
}
