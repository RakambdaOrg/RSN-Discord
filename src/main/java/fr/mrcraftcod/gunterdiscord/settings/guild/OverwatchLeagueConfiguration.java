package fr.mrcraftcod.gunterdiscord.settings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.settings.types.ChannelConfiguration;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("FieldMayBeFinal")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverwatchLeagueConfiguration{
	@JsonProperty("notificationChannel")
	private ChannelConfiguration notificationChannel;
	@JsonProperty("notifiedMatches")
	private Set<Integer> notifiedMatches = new HashSet<>();
	
	public Optional<ChannelConfiguration> getNotificationChannel(){
		return Optional.ofNullable(this.notificationChannel);
	}
	
	public void setNotificationChannel(@Nullable final ChannelConfiguration value){
		this.notificationChannel = value;
	}
	
	public Set<Integer> getNotifiedMatches(){
		return this.notifiedMatches;
	}
	
	public void setNotifiedMatch(final int id){
		this.notifiedMatches.add(id);
	}
}
