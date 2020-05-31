package fr.raksrinana.rsndiscord.utils.externaltodos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class GetTodoResponse{
	@JsonProperty("code")
	private int code;
	@JsonProperty("message")
	private String message;
	@JsonProperty("todos")
	private List<Todo> todos;
}
