package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.settings.CompositeConfiguration;
import fr.raksrinana.rsndiscord.settings.guild.participation.MessageParticipation;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.json.ISO8601LocalDateDeserializer;
import fr.raksrinana.rsndiscord.utils.json.ISO8601LocalDateKeyDeserializer;
import fr.raksrinana.rsndiscord.utils.json.ISO8601LocalDateKeySerializer;
import fr.raksrinana.rsndiscord.utils.json.ISO8601LocalDateSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.TextChannel;
import java.time.LocalDate;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class ParticipationConfiguration implements CompositeConfiguration{
	@JsonProperty("messagesParticipation")
	@JsonSerialize(keyUsing = ISO8601LocalDateKeySerializer.class)
	@JsonDeserialize(keyUsing = ISO8601LocalDateKeyDeserializer.class)
	private Map<LocalDate, MessageParticipation> messagesParticipation = new HashMap<>();
	@JsonProperty("ignoredChannels")
	@Setter
	private Set<ChannelConfiguration> ignoredChannels = new HashSet<>();
	@JsonProperty("reportChannel")
	@Setter
	private ChannelConfiguration reportChannel;
	@JsonProperty("reportedDays")
	@JsonSerialize(contentUsing = ISO8601LocalDateSerializer.class)
	@JsonDeserialize(contentUsing = ISO8601LocalDateDeserializer.class)
	@Setter
	private Set<LocalDate> reportedDays = new HashSet<>();
	
	public Optional<MessageParticipation> getDay(LocalDate date){
		return Optional.ofNullable(this.messagesParticipation.get(date));
	}
	
	public MessageParticipation getOrCreateDay(LocalDate date){
		return this.messagesParticipation.computeIfAbsent(date, key -> new MessageParticipation(date));
	}
	
	public boolean isChannelIgnored(TextChannel channel){
		return this.ignoredChannels.stream().anyMatch(ignoredChannel -> Objects.equals(ignoredChannel.getChannelId(), channel.getIdLong()));
	}
	
	public Optional<ChannelConfiguration> getReportChannel(){
		return Optional.ofNullable(this.reportChannel);
	}
}
