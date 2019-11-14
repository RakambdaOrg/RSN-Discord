package fr.raksrinana.rsndiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class UserRoleConfiguration{
	@JsonProperty("user")
	private UserConfiguration user;
	@JsonProperty("role")
	private RoleConfiguration role;
	
	public UserRoleConfiguration(final User user, final Role role){
		this(user.getIdLong(), role.getIdLong());
	}
	
	private UserRoleConfiguration(final long userId, final long roleId){
		this.user = new UserConfiguration(userId);
		this.role = new RoleConfiguration(roleId);
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(this.getUser()).append(this.getRole()).toHashCode();
	}
	
	@Override
	public boolean equals(final Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof UserRoleConfiguration)){
			return false;
		}
		final var that = (UserRoleConfiguration) o;
		return new EqualsBuilder().append(this.getUser(), that.getUser()).append(this.getRole(), that.getRole()).isEquals();
	}
}
