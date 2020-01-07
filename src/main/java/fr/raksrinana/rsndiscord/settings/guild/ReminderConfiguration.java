package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import fr.raksrinana.rsndiscord.utils.json.LocalDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.LocalDateTimeSerializer;
import fr.raksrinana.rsndiscord.utils.reminder.ReminderTag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class ReminderConfiguration{
	@JsonProperty("tag")
	private ReminderTag tag = ReminderTag.NONE;
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
	@JsonProperty("data")
	private Map<String, String> data = new HashMap<>();
	
	public ReminderConfiguration(@NonNull User user, @NonNull TextChannel channel, @NonNull LocalDateTime notifyDate, @NonNull String message){
		this(user, channel, notifyDate, message, ReminderTag.NONE, null);
	}
	
	public ReminderConfiguration(@NonNull User user, @NonNull TextChannel channel, @NonNull LocalDateTime notifyDate, @NonNull String message, @NonNull ReminderTag tag, Map<String, String> data){
		this(new UserConfiguration(user), new ChannelConfiguration(channel), notifyDate, message, tag, data);
	}
	
	public ReminderConfiguration(@NonNull UserConfiguration user, @NonNull ChannelConfiguration channel, @NonNull LocalDateTime notifyDate, @NonNull String message, @NonNull ReminderTag tag, Map<String, String> data){
		this.user = user;
		this.channel = channel;
		this.notifyDate = notifyDate;
		this.message = message;
		this.tag = tag;
		this.data = data;
	}
}
