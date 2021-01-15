package fr.raksrinana.rsndiscord.settings.guild.participation;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.settings.ICompositeConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.json.ISO8601LocalDateDeserializer;
import fr.raksrinana.rsndiscord.utils.json.ISO8601LocalDateKeyDeserializer;
import fr.raksrinana.rsndiscord.utils.json.ISO8601LocalDateKeySerializer;
import fr.raksrinana.rsndiscord.utils.json.ISO8601LocalDateSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import java.time.LocalDate;
import java.util.*;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class ParticipationConfiguration implements ICompositeConfiguration{
	@JsonProperty("chatParticipation")
	@JsonAlias("messagesParticipation")
	@JsonSerialize(keyUsing = ISO8601LocalDateKeySerializer.class)
	@JsonDeserialize(keyUsing = ISO8601LocalDateKeyDeserializer.class)
	private final Map<LocalDate, ChatParticipation> chatParticipation = new HashMap<>();
	@JsonProperty("voiceParticipation")
	@JsonSerialize(keyUsing = ISO8601LocalDateKeySerializer.class)
	@JsonDeserialize(keyUsing = ISO8601LocalDateKeyDeserializer.class)
	private final Map<LocalDate, VoiceParticipation> voiceParticipation = new HashMap<>();
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
	
	public Optional<ChatParticipation> getChatDay(LocalDate date){
		return ofNullable(this.chatParticipation.get(date));
	}
	
	public Optional<VoiceParticipation> getVoiceDay(LocalDate date){
		return ofNullable(this.voiceParticipation.get(date));
	}
	
	public ChatParticipation getOrCreateChatDay(LocalDate date){
		return this.chatParticipation.computeIfAbsent(date, key -> new ChatParticipation(date));
	}
	
	public VoiceParticipation getOrCreateVoiceDay(LocalDate date){
		return this.voiceParticipation.computeIfAbsent(date, key -> new VoiceParticipation(date));
	}
	
	public boolean isChannelIgnored(TextChannel channel){
		return this.ignoredChannels.stream().anyMatch(ignoredChannel -> Objects.equals(ignoredChannel.getChannelId(), channel.getIdLong()));
	}
	
	public boolean isChannelIgnored(VoiceChannel channel){
		return this.ignoredChannels.stream().anyMatch(ignoredChannel -> Objects.equals(ignoredChannel.getChannelId(), channel.getIdLong()));
	}
	
	public Optional<ChannelConfiguration> getReportChannel(){
		return ofNullable(this.reportChannel);
	}
}
