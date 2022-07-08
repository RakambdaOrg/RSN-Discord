package fr.raksrinana.rsndiscord.schedule.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.schedule.api.IScheduleHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

@AllArgsConstructor
@Getter
public abstract class SimpleScheduleHandler implements IScheduleHandler{
	@JsonProperty("id")
	private UUID id;
	@JsonProperty("attempts")
	private int attempts = 0;
	@JsonProperty("maxAttempts")
	private int maxAttempts = 15;
	
	public SimpleScheduleHandler(){
		this(UUID.randomUUID(), 0, 15);
	}
	
	@Override
	@NotNull
	public String getSchedulerId(){
		return getId().toString();
	}
	
	@Override
	public boolean increaseAttempt(){
		return (attempts++) > maxAttempts;
	}
}

