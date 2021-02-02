package fr.raksrinana.rsndiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.settings.IAtomicConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class UserConfiguration implements IAtomicConfiguration{
	@JsonProperty("userId")
	@Setter
	private long userId;
	
	public UserConfiguration(@NotNull User user){
		this(user.getIdLong());
	}
	
	public UserConfiguration(long userId){
		this.userId = userId;
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(getUserId()).toHashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof UserConfiguration)){
			return false;
		}
		var that = (UserConfiguration) o;
		return new EqualsBuilder().append(getUserId(), that.getUserId()).isEquals();
	}
	
	@Override
	public String toString(){
		return "User(" + getUserId() + ')';
	}
	
	@Override
	public boolean shouldBeRemoved(){
		return getUser().isEmpty();
	}
	
	@NotNull
	public Optional<User> getUser(){
		return ofNullable(Main.getJda().retrieveUserById(getUserId()).complete());
	}
	
	public void setUser(@NotNull User user){
		setUserId(user.getIdLong());
	}
}
