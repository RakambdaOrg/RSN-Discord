package fr.raksrinana.rsndiscord.utils.externaltodos;

import fr.raksrinana.rsndiscord.utils.Actions;
import fr.raksrinana.rsndiscord.utils.Utilities;
import fr.raksrinana.rsndiscord.utils.log.Log;
import fr.raksrinana.utils.http.requestssenders.get.ObjectGetRequestSender;
import kong.unirest.GenericType;
import kong.unirest.HeaderNames;
import kong.unirest.Unirest;
import java.util.Optional;

public class ExternalTodosUtils{
	public static Optional<GetTodoResponse> getTodos(String endpoint, String token){
		final var request = Unirest.get(endpoint + "/todos");
		Optional.ofNullable(token).ifPresent(t -> request.header(HeaderNames.AUTHORIZATION, "Bearer " + t));
		final var handler = new ObjectGetRequestSender<GetTodoResponse>(new GenericType<>(){}, request).getRequestHandler();
		handler.getResult().getParsingError().ifPresent(error -> {
			Actions.sendPrivateMessage(Utilities.RAKSRINANA_ACCOUNT, "Failed to parse external todos response", Utilities.throwableToEmbed(error).build());
			Log.getLogger(null).warn("Failed to parse external todos response", error);
		});
		if(handler.getResult().isSuccess()){
			return Optional.of(handler.getRequestResult());
		}
		return Optional.empty();
	}
	
	public static Optional<SetStatusResponse> setStatus(String endpoint, String token, Todo todo, Status status){
		final var request = Unirest.get(endpoint + "/todos/status/set").queryString("id", todo.getId()).queryString("status", status.name());
		Optional.ofNullable(token).ifPresent(t -> request.header(HeaderNames.AUTHORIZATION, "Bearer " + t));
		final var handler = new ObjectGetRequestSender<SetStatusResponse>(new GenericType<>(){}, request).getRequestHandler();
		handler.getResult().getParsingError().ifPresent(error -> {
			Actions.sendPrivateMessage(Utilities.RAKSRINANA_ACCOUNT, "Failed to parse external todos response", Utilities.throwableToEmbed(error).build());
			Log.getLogger(null).warn("Failed to parse external todos response", error);
		});
		if(handler.getResult().isSuccess()){
			return Optional.of(handler.getRequestResult());
		}
		return Optional.empty();
	}
}
