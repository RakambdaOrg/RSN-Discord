package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.settings.AtomicConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import fr.raksrinana.rsndiscord.utils.json.LocalDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.LocalDateTimeSerializer;
import fr.raksrinana.rsndiscord.utils.schedule.ScheduleTag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class ScheduleConfiguration implements AtomicConfiguration{
	@JsonProperty("tag")
	private ScheduleTag tag = ScheduleTag.NONE;
	@JsonProperty("user")
	private UserConfiguration user;
	@JsonProperty("channel")
	private ChannelConfiguration channel;
	@JsonProperty("scheduleDate")
	@JsonAlias({"notifyDate"})
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@Setter
	private LocalDateTime scheduleDate;
	@JsonProperty("message")
	private String message;
	@JsonProperty("reminderCountdownMessage")
	@Setter
	private MessageConfiguration reminderCountdownMessage;
	@JsonProperty("data")
	private Map<String, String> data = new HashMap<>();
	
	protected ScheduleConfiguration(@NonNull User user, @NonNull TextChannel channel, @NonNull LocalDateTime scheduleDate, @NonNull String message){
		this(user, channel, scheduleDate, message, ScheduleTag.NONE, null);
	}
	
	protected ScheduleConfiguration(@NonNull User user, @NonNull TextChannel channel, @NonNull LocalDateTime scheduleDate, @NonNull String message, @NonNull ScheduleTag tag, Map<String, String> data){
		this(new UserConfiguration(user), new ChannelConfiguration(channel), scheduleDate, message, tag, data);
	}
	
	protected ScheduleConfiguration(@NonNull UserConfiguration user, @NonNull ChannelConfiguration channel, @NonNull LocalDateTime scheduleDate, @NonNull String message, @NonNull ScheduleTag tag, Map<String, String> data){
		this.user = user;
		this.channel = channel;
		this.scheduleDate = scheduleDate;
		this.message = message;
		this.tag = tag;
		this.data = data;
	}
	
	protected ScheduleConfiguration(@NonNull User user, @NonNull TextChannel channel, @NonNull LocalDateTime scheduleDate, @NonNull String message, @NonNull ScheduleTag tag){
		this(new UserConfiguration(user), new ChannelConfiguration(channel), scheduleDate, message, tag, null);
	}
	
	@Override
	public boolean shouldBeRemoved(){
		return getUser().shouldBeRemoved() || getChannel().shouldBeRemoved();
	}
	
	@Override
	public String toString(){
		return new StringJoiner(", ", ScheduleConfiguration.class.getSimpleName() + "[", "]").add("tag=" + tag).add("user=" + user).add("channel=" + channel).add("scheduleDate=" + scheduleDate).toString();
	}
}
