package fr.mrcraftcod.gunterdiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.Main;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Created by mrcraftcod (MrCraftCod - zerderr@gmail.com) on 2019-06-23.
 *
 * @author Thomas Couchoud
 * @since 2019-06-23
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserConfiguration{
	@JsonProperty("userId")
	private long userId;
	
	public UserConfiguration(){
	}
	
	public UserConfiguration(final User user){
		this(user.getIdLong());
	}
	
	public UserConfiguration(final long userId){
		this.userId = userId;
	}
	
	@Override
	public String toString(){
		return this.getUser().map(User::getAsMention).orElse("");
	}
	
	@Nonnull
	public Optional<User> getUser(){
		return Optional.ofNullable(Main.getJDA().getUserById(this.getUserId()));
	}
	
	public void setUser(@Nonnull User user){
		this.setUser(user.getIdLong());
	}
	
	private void setUser(long userId){
		this.userId = userId;
	}
	
	public long getUserId(){
		return this.userId;
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
		UserConfiguration that = (UserConfiguration) o;
		return new EqualsBuilder().append(getUserId(), that.getUserId()).isEquals();
	}
}
