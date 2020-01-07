package fr.raksrinana.rsndiscord.settings.guild.trombinoscope;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.AtomicConfiguration;
import fr.raksrinana.rsndiscord.settings.types.UserConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
public class PhotoEntryConfiguration implements AtomicConfiguration{
	@JsonProperty("user")
	private UserConfiguration user;
	@JsonProperty("photo")
	private String photo;
	
	public PhotoEntryConfiguration(@NonNull final User user, @NonNull final String photo){
		this.user = new UserConfiguration(user);
		this.photo = photo;
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(this.user).append(this.getPhoto()).toHashCode();
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
	
	@Override
	public boolean shouldBeRemoved(){
		return getUser().shouldBeRemoved();
	}
}
