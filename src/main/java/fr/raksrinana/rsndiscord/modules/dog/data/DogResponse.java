package fr.raksrinana.rsndiscord.modules.dog.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@ToString
public class DogResponse{
	public static final String SUCCESS = "success";
	@JsonProperty("status")
	private String status;
	@JsonProperty("message")
	private String message;
	
	public boolean isSuccess(){
		return Objects.equals(getStatus(), SUCCESS);
	}
}
