package fr.raksrinana.rsndiscord.settings.guild.schedule;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.schedule.ScheduleTag;
import fr.raksrinana.rsndiscord.settings.IAtomicConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.settings.types.MessageConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import fr.raksrinana.rsndiscord.utils.json.ZonedDateTimeDeserializer;
import fr.raksrinana.rsndiscord.utils.json.ZonedDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import static fr.raksrinana.rsndiscord.schedule.ScheduleTag.NONE;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class ScheduleConfiguration implements IAtomicConfiguration{
	@JsonProperty("tag")
	private ScheduleTag tag = NONE;
	@JsonProperty("user")
	private UserConfiguration user;
	@JsonProperty("channel")
	private ChannelConfiguration channel;
	@JsonProperty("scheduleDate")
	@JsonAlias({"notifyDate"})
	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	@JsonSerialize(using = ZonedDateTimeSerializer.class)
	@Setter
	private ZonedDateTime scheduleDate;
	@JsonProperty("message")
	private String message;
	@JsonProperty("reminderCountdownMessage")
	@Setter
	private MessageConfiguration reminderCountdownMessage;
	@JsonProperty("data")
	private Map<String, String> data = new HashMap<>();
	
	protected ScheduleConfiguration(@NotNull User user, @Nullable TextChannel channel, @NotNull ZonedDateTime scheduleDate, @NotNull String message){
		this(user, channel, scheduleDate, message, NONE, null);
	}
	
	protected ScheduleConfiguration(@NotNull User user, @Nullable TextChannel channel, @NotNull ZonedDateTime scheduleDate, @NotNull String message, @NotNull ScheduleTag tag, @Nullable Map<String, String> data){
		this(new UserConfiguration(user), channel == null ? null : new ChannelConfiguration(channel), scheduleDate, message, tag, data);
	}
	
	protected ScheduleConfiguration(@NotNull UserConfiguration user, @Nullable ChannelConfiguration channel, @NotNull ZonedDateTime scheduleDate, @NotNull String message, @NotNull ScheduleTag tag, @Nullable Map<String, String> data){
		this.user = user;
		this.channel = channel;
		this.scheduleDate = scheduleDate;
		this.message = message;
		this.tag = tag;
		this.data = data;
	}
	
	protected ScheduleConfiguration(@NotNull User user, @NotNull TextChannel channel, @NotNull ZonedDateTime scheduleDate, @NotNull String message, @NotNull ScheduleTag tag){
		this(new UserConfiguration(user), new ChannelConfiguration(channel), scheduleDate, message, tag, null);
	}
	
	@NotNull
	public Optional<ChannelConfiguration> getChannel(){
		return Optional.ofNullable(channel);
	}
	
	@Override
	public boolean shouldBeRemoved(){
		return getUser().shouldBeRemoved() || getChannel().map(ChannelConfiguration::shouldBeRemoved).orElse(false);
	}
	
	@Override
	public String toString(){
		return new StringJoiner(", ", ScheduleConfiguration.class.getSimpleName() + "[", "]").add("tag=" + tag).add("user=" + user).add("channel=" + channel).add("scheduleDate=" + scheduleDate).toString();
	}
}
