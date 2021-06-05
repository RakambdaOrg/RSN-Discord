package fr.raksrinana.rsndiscord.schedule.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.schedule.ScheduleResult;
import fr.raksrinana.rsndiscord.schedule.base.SimpleScheduleHandler;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import fr.raksrinana.rsndiscord.utils.json.converter.ZonedDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.converter.ZonedDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;
import static fr.raksrinana.rsndiscord.schedule.ScheduleResult.*;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static net.dv8tion.jda.api.requests.ErrorResponse.UNKNOWN_USER;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("UnbanMember")
@AllArgsConstructor
@NoArgsConstructor
public class UnbanMemberScheduleHandler extends SimpleScheduleHandler{
	@JsonProperty("userId")
	private long userId;
	@JsonProperty("requestDeletionDate")
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	@JsonSerialize(using = ZonedDateTimeSerializer.class)
	private ZonedDateTime requestUnbanDate;
	
	@Override
	public CompletableFuture<ScheduleResult> process(@NotNull Guild guild){
		if(ZonedDateTime.now().isBefore(requestUnbanDate)){
			return completedFuture(DELAYED);
		}
		
		return JDAWrappers.unban(guild, Long.toUnsignedString(userId)).submit()
				.thenApply(e -> COMPLETED)
				.exceptionally(e -> {
					if(e instanceof ErrorResponseException re && re.getErrorResponse() == UNKNOWN_USER){
						return COMPLETED;
					}
					return FAILED;
				});
	}
}
