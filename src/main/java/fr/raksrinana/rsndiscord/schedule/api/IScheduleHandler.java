package fr.raksrinana.rsndiscord.schedule.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.raksrinana.rsndiscord.schedule.ScheduleResult;
import fr.raksrinana.rsndiscord.schedule.impl.DeleteChannelScheduleHandler;
import fr.raksrinana.rsndiscord.schedule.impl.DeleteMessageScheduleHandler;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = DeleteChannelScheduleHandler.class, name = "DeleteChannel"),
		@JsonSubTypes.Type(value = DeleteMessageScheduleHandler.class, name = "DeleteMessage"),
})
public interface IScheduleHandler{
	boolean increaseAttempt();
	
	String getSchedulerId();
	
	CompletableFuture<ScheduleResult> process(@NotNull Guild guild);
}
