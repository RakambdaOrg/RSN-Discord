package fr.raksrinana.rsndiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.types.ChannelConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class OverwatchLeagueConfiguration{
	@JsonProperty("notificationChannel")
	@Setter
	private ChannelConfiguration notificationChannel;
	@JsonProperty("notifiedMatches")
	@Getter
	private Set<Integer> notifiedMatches = new HashSet<>();
	
	public Optional<ChannelConfiguration> getNotificationChannel(){
		return Optional.ofNullable(this.notificationChannel);
	}
	
	public void setNotifiedMatch(final int id){
		this.notifiedMatches.add(id);
	}
}
