package fr.raksrinana.rsndiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.utils.json.LocalDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class ReminderConfiguration{
	@JsonProperty("user")
	private UserConfiguration user;
	@JsonProperty("channel")
	private ChannelConfiguration channel;
	@JsonProperty("notifyDate")
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	private LocalDateTime notifyDate;
	@JsonProperty("message")
	private String message;
	@JsonProperty("reminderCountdownMessage")
	@Setter
	private MessageConfiguration reminderCountdownMessage;
	
	public ReminderConfiguration(@NonNull User user, @NonNull TextChannel channel, @NonNull LocalDateTime notifyDate, @NonNull String message){
		this(new UserConfiguration(user), new ChannelConfiguration(channel), notifyDate, message);
	}
	
	public ReminderConfiguration(@NonNull UserConfiguration user, @NonNull ChannelConfiguration channel, @NonNull LocalDateTime notifyDate, @NonNull String message){
		this.user = user;
		this.channel = channel;
		this.notifyDate = notifyDate;
		this.message = message;
	}
}
