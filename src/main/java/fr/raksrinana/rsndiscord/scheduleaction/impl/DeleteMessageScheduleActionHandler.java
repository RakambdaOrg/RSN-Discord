package fr.raksrinana.rsndiscord.scheduleaction.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.scheduleaction.ScheduleActionResult;
import fr.raksrinana.rsndiscord.scheduleaction.base.SimpleScheduleActionHandler;
import fr.raksrinana.rsndiscord.utils.jda.JDAWrappers;
import fr.raksrinana.rsndiscord.utils.json.converter.ZonedDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.converter.ZonedDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.jetbrains.annotations.NotNull;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import static fr.raksrinana.rsndiscord.scheduleaction.ScheduleActionResult.*;
import static java.util.concurrent.CompletableFuture.completedFuture;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeName("DeleteMessage")
@AllArgsConstructor
@NoArgsConstructor
public class DeleteMessageScheduleActionHandler extends SimpleScheduleActionHandler{
	@JsonProperty("channelId")
	private long channelId;
	@JsonProperty("messageId")
	private long messageId;
	@JsonProperty("requestDeletionDate")
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	@JsonSerialize(using = ZonedDateTimeSerializer.class)
	private ZonedDateTime requestDeletionDate;
	
	@Override
	public CompletableFuture<ScheduleActionResult> process(@NotNull Guild guild){
		if(ZonedDateTime.now().isBefore(requestDeletionDate)){
			return completedFuture(DELAYED);
		}
		
		return Optional.ofNullable(guild.getTextChannelById(channelId))
				.map(channel -> channel.retrieveMessageById(messageId).submit()
						.thenCompose(message -> JDAWrappers.delete(message).submit())
						.thenApply(empty -> COMPLETED)
						.exceptionally(e -> {
							if(e instanceof ErrorResponseException responseException){
								return switch(responseException.getErrorResponse()){
									case UNKNOWN_MESSAGE, UNKNOWN_CHANNEL -> COMPLETED;
									default -> FAILED;
								};
							}
							return FAILED;
						})
				).orElse(completedFuture(COMPLETED));
	}
}
