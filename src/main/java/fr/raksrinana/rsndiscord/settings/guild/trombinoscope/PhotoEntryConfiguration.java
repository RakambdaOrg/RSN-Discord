package fr.raksrinana.rsndiscord.settings.guild.trombinoscope;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
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
	
	public PhotoEntryConfiguration(@Nonnull final User user, @Nonnull final String photo){
		this.user = new UserConfiguration(user);
		this.photo = photo;
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(this.user).append(this.getPhoto()).toHashCode();
	}
	
	@Nonnull
	public String getPhoto(){
		return this.photo;
	}
	
	@Override
	public boolean equals(final Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof PhotoEntryConfiguration)){
			return false;
		}
		final var that = (PhotoEntryConfiguration) o;
		return new EqualsBuilder().append(this.user, that.user).append(this.getPhoto(), that.getPhoto()).isEquals();
	}
	
	public long getUserId(){
		return this.user.getUserId();
	}
}
