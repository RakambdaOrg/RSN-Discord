package fr.mrcraftcod.gunterdiscord.newSettings.guild.trombinoscope;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.newSettings.types.UserConfiguration;
import net.dv8tion.jda.api.entities.User;
import javax.annotation.Nonnull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PhotoEntryConfiguration{
	@JsonProperty("user")
	private UserConfiguration user;
	@JsonProperty("photo")
	private String photo;
	
	public PhotoEntryConfiguration(){
	}
	
	public PhotoEntryConfiguration(@Nonnull User user, @Nonnull String photo){
		this.user = new UserConfiguration(user);
		this.photo = photo;
	}
}
