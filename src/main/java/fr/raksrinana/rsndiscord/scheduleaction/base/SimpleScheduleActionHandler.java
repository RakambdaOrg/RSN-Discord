package fr.raksrinana.rsndiscord.scheduleaction.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.scheduleaction.api.IScheduleActionHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

@AllArgsConstructor
@Getter
public abstract class SimpleScheduleActionHandler implements IScheduleActionHandler{
	@JsonProperty("id")
	private UUID id;
	
	public SimpleScheduleActionHandler(){
		this(UUID.randomUUID());
	}
	
	@Override
	@NotNull
	public String getSchedulerId(){
		return getId().toString();
	}
}

