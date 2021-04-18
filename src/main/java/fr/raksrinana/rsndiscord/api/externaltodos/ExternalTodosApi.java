package fr.raksrinana.rsndiscord.api.externaltodos;

import fr.raksrinana.rsndiscord.api.externaltodos.data.GetTodoResponse;
import fr.raksrinana.rsndiscord.api.externaltodos.data.SetStatusResponse;
import fr.raksrinana.rsndiscord.api.externaltodos.data.Status;
import fr.raksrinana.rsndiscord.api.externaltodos.data.Todo;
import fr.raksrinana.rsndiscord.log.Log;
import fr.raksrinana.rsndiscord.utils.Utilities;
import kong.unirest.GenericType;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static kong.unirest.HeaderNames.AUTHORIZATION;

public class ExternalTodosApi{
	@NotNull
	public static Optional<GetTodoResponse> getTodos(@NotNull String endpoint, @Nullable String token){
		var builder = Unirest.get(endpoint + "/todos");
		ofNullable(token).ifPresent(t -> builder.header(AUTHORIZATION, "Bearer " + t));
		var request = builder.asObject(new GenericType<GetTodoResponse>(){});
		
		request.getParsingError().ifPresent(error -> {
			Utilities.reportException("Failed to parse external todos response", error);
			Log.getLogger().warn("Failed to parse external todos response", error);
		});
		if(!request.isSuccess()){
			return empty();
		}
		
		return Optional.of(request.getBody());
	}
	
	@NotNull
	public static Optional<SetStatusResponse> setStatus(@NotNull String endpoint, @Nullable String token, @NotNull Todo todo, @NotNull Status status){
		var body = new JSONObject();
		body.put("id", todo.getId());
		body.put("status", status.name());
		
		var builder = Unirest.post(endpoint + "/todos/status/set").body(body);
		ofNullable(token).ifPresent(t -> builder.header(AUTHORIZATION, "Bearer " + t));
		var request = builder.asObject(new GenericType<SetStatusResponse>(){});
		
		request.getParsingError().ifPresent(error -> {
			Utilities.reportException("Failed to parse external todos response", error);
			Log.getLogger().warn("Failed to parse external todos response", error);
		});
		if(!request.isSuccess()){
			return empty();
		}
		
		return Optional.of(request.getBody());
	}
}
