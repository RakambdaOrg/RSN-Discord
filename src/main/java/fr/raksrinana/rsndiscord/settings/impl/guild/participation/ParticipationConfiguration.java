package fr.raksrinana.rsndiscord.settings.impl.guild.participation;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.raksrinana.rsndiscord.settings.api.ICompositeConfiguration;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import fr.raksrinana.rsndiscord.utils.json.converter.ISO8601LocalDateDeserializer;
import fr.raksrinana.rsndiscord.utils.json.converter.ISO8601LocalDateKeyDeserializer;
import fr.raksrinana.rsndiscord.utils.json.converter.ISO8601LocalDateKeySerializer;
import fr.raksrinana.rsndiscord.utils.json.converter.ISO8601LocalDateSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.jetbrains.annotations.NotNull;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
	
	@NotNull
	public Optional<ChatParticipation> getChatDay(@NotNull LocalDate date){
		return ofNullable(chatParticipation.get(date));
	}
	
	@NotNull
	public Optional<VoiceParticipation> getVoiceDay(@NotNull LocalDate date){
		return ofNullable(voiceParticipation.get(date));
	}
	
	@NotNull
	public ChatParticipation getOrCreateChatDay(@NotNull LocalDate date){
		return chatParticipation.computeIfAbsent(date, key -> new ChatParticipation(date));
	}
	
	@NotNull
	public VoiceParticipation getOrCreateVoiceDay(@NotNull LocalDate date){
		return voiceParticipation.computeIfAbsent(date, key -> new VoiceParticipation(date));
	}
	
	public boolean isChannelIgnored(@NotNull TextChannel channel){
		return ignoredChannels.stream().anyMatch(ignoredChannel -> Objects.equals(ignoredChannel.getChannelId(), channel.getIdLong()));
	}
	
	public boolean isChannelIgnored(@NotNull VoiceChannel channel){
		return ignoredChannels.stream().anyMatch(ignoredChannel -> Objects.equals(ignoredChannel.getChannelId(), channel.getIdLong()));
	}
	
	@NotNull
	public Optional<ChannelConfiguration> getReportChannel(){
		return ofNullable(reportChannel);
	}
}
