package fr.raksrinana.rsndiscord.scheduleaction.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fr.raksrinana.rsndiscord.scheduleaction.ScheduleActionResult;
import fr.raksrinana.rsndiscord.scheduleaction.impl.DeleteChannelScheduleActionHandler;
import fr.raksrinana.rsndiscord.scheduleaction.impl.DeleteMessageScheduleActionHandler;
import fr.raksrinana.rsndiscord.scheduleaction.impl.UnbanMemberScheduleActionHandler;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.CompletableFuture;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
		@JsonSubTypes.Type(value = DeleteChannelScheduleActionHandler.class, name = "DeleteChannel"),
		@JsonSubTypes.Type(value = DeleteMessageScheduleActionHandler.class, name = "DeleteMessage"),
		@JsonSubTypes.Type(value = UnbanMemberScheduleActionHandler.class, name = "UnbanMember"),
})
public interface IScheduleActionHandler{
	String getSchedulerId();
	
	CompletableFuture<ScheduleActionResult> process(@NotNull Guild guild);
}
