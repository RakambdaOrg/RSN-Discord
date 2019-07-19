package fr.mrcraftcod.gunterdiscord.settings.guild.trombinoscope;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.settings.types.UserConfiguration;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
	
	@Nonnull
	public String getPhoto(){
		return photo;
	}
	
	public long getUserId(){
		return this.user.getUserId();
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(user).append(getPhoto()).toHashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof PhotoEntryConfiguration)){
			return false;
		}
		PhotoEntryConfiguration that = (PhotoEntryConfiguration) o;
		return new EqualsBuilder().append(user, that.user).append(getPhoto(), that.getPhoto()).isEquals();
	}
}
