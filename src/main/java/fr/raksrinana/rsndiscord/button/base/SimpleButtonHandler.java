package fr.raksrinana.rsndiscord.button.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.button.api.IButtonHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

@AllArgsConstructor
@Getter
public abstract class SimpleButtonHandler implements IButtonHandler{
	@JsonProperty("id")
	private String id;
	
	public SimpleButtonHandler(){
		this(UUID.randomUUID().toString());
	}
	
	@Override
	@NotNull
	public String getButtonId(){
		return getId();
	}
}

