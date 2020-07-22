package fr.raksrinana.rsndiscord.utils.uselessfacts;

import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.utils.http.requestssenders.get.ObjectGetRequestSender;
import kong.unirest.GenericType;
import kong.unirest.Unirest;
import java.util.Optional;

public class UselessFactsUtils{
	public static Optional<UselessFact> getFact(){
		Log.getLogger(null).debug("Requesting random fact");
		final var handler = new ObjectGetRequestSender<>(new GenericType<UselessFact>(){}, Unirest.get("https://uselessfacts.jsph.pl/random.json").queryString("language", "en")).getRequestHandler();
		handler.getResult().getParsingError().ifPresent(error -> {
			Utilities.reportException("Failed to parse UselessFacts response", error);
			Log.getLogger(null).warn("Failed to parse UselessFacts response", error);
		});
		if(handler.getStatus() == 200){
			return Optional.ofNullable(handler.getRequestResult());
		}
		return Optional.empty();
	}
}
