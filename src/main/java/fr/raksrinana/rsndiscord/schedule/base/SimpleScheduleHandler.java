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
	
	public SimpleScheduleHandler(){
		this(UUID.randomUUID());
	}
	
	@Override
	@NotNull
	public String getSchedulerId(){
		return getId().toString();
	}
}

