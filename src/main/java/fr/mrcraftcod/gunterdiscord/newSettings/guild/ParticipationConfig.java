package fr.mrcraftcod.gunterdiscord.newSettings.guild;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.newSettings.guild.participation.EmotesParticipation;
import fr.mrcraftcod.gunterdiscord.newSettings.types.UserConfiguration;
import fr.mrcraftcod.gunterdiscord.settings.configs.done.EmotesParticipationPinConfig;
import net.dv8tion.jda.api.entities.Guild;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParticipationConfig{
	@JsonProperty("emotes")
	private List<EmotesParticipation> emotesParticipation = new ArrayList<>();
	@JsonProperty("users")
	private List<EmotesParticipation> usersParticipation = new ArrayList<>();
	@JsonProperty("usersPenned")
	private List<UserConfiguration> usersPinned = new ArrayList<>();
	
	public ParticipationConfig(){
	}
	
	public void mapOldConf(Guild guild){
		this.usersPinned = new EmotesParticipationPinConfig(guild).getAsList().orElse(List.of()).stream().map(UserConfiguration::new).collect(Collectors.toList());
	}
	
	public void setUsersPinned(@Nonnull List<UserConfiguration> usersPinned){
		this.usersPinned = usersPinned;
	}
	
	@Nonnull
	public List<UserConfiguration> getUsersPinned(){
		return this.usersPinned;
	}
}
