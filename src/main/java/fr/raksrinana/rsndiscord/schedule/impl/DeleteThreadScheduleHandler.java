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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import static fr.raksrinana.rsndiscord.schedule.ScheduleResult.COMPLETED;
import static fr.raksrinana.rsndiscord.schedule.ScheduleResult.DELAYED;
import static fr.raksrinana.rsndiscord.schedule.ScheduleResult.FAILED;
import static fr.raksrinana.rsndiscord.schedule.ScheduleResult.WARN;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static net.dv8tion.jda.api.requests.ErrorResponse.UNKNOWN_CHANNEL;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("DeleteThread")
@AllArgsConstructor
@NoArgsConstructor
@Log4j2
@Deprecated
public class DeleteThreadScheduleHandler extends SimpleScheduleHandler{
	@JsonProperty("threadId")
	@Getter
	private long threadId;
	@JsonProperty("requestDeletionDate")
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	@JsonSerialize(using = ZonedDateTimeSerializer.class)
	private ZonedDateTime requestDeletionDate;
	
	@Override
	public CompletableFuture<ScheduleResult> process(@NotNull Guild guild){
		if(ZonedDateTime.now().isBefore(requestDeletionDate)){
			return completedFuture(DELAYED);
		}
		
		return Optional.ofNullable(guild.getThreadChannelById(threadId))
				.map(thread -> JDAWrappers.delete(thread).submit()
						.thenApply(empty -> COMPLETED)
						.exceptionally(this::handleException)
				).orElseGet(() -> {
					log.warn("Thread with ID {} couldn't be found", threadId);
					return completedFuture(WARN);
				});
	}
	
	@NotNull
	private ScheduleResult handleException(@NotNull Throwable e){
		if(e instanceof CompletionException completionException){
			return handleException(completionException.getCause());
		}
		
		if(e instanceof ErrorResponseException re){
			if(re.getErrorResponse() == UNKNOWN_CHANNEL){
				return COMPLETED;
			}
		}
		
		log.error("Got unexpected exception", e);
		return FAILED;
	}
}
