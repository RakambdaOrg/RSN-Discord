package fr.raksrinana.rsndiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.settings.IAtomicConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class UserRoleConfiguration implements IAtomicConfiguration{
	@JsonProperty("user")
	private UserConfiguration user;
	@JsonProperty("role")
	private RoleConfiguration role;
	
	public UserRoleConfiguration(@NotNull User user, @NotNull Role role){
		this(user.getIdLong(), role.getIdLong());
	}
	
	private UserRoleConfiguration(long userId, long roleId){
		user = new UserConfiguration(userId);
		role = new RoleConfiguration(roleId);
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(getUser()).append(getRole()).toHashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof UserRoleConfiguration)){
			return false;
		}
		var that = (UserRoleConfiguration) o;
		return new EqualsBuilder().append(getUser(), that.getUser()).append(getRole(), that.getRole()).isEquals();
	}
	
	@Override
	public String toString(){
		return "UserRole(" + getUser().toString() + '|' + getRole() + ')';
	}
	
	@Override
	public boolean shouldBeRemoved(){
		return getUser().shouldBeRemoved() || getRole().shouldBeRemoved();
	}
}
