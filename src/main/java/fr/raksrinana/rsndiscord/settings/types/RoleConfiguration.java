package fr.raksrinana.rsndiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.settings.AtomicConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Role;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class RoleConfiguration implements AtomicConfiguration{
	@JsonProperty("roleId")
	@Setter
	private long roleId;
	
	public RoleConfiguration(final Role role){
		this(role.getIdLong());
	}
	
	public RoleConfiguration(final long roleId){
		this.roleId = roleId;
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(this.getRoleId()).toHashCode();
	}
	
	@Override
	public boolean equals(final Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof RoleConfiguration)){
			return false;
		}
		final var that = (RoleConfiguration) o;
		return new EqualsBuilder().append(this.getRoleId(), that.getRoleId()).isEquals();
	}
	
	@Override
	public String toString(){
		return this.getRole().map(Role::getAsMention).orElse("<Unknown role>");
	}
	
	@Override
	public boolean shouldBeRemoved(){
		return getRole().isEmpty();
	}
	
	@NonNull
	public Optional<Role> getRole(){
		return Optional.ofNullable(Main.getJda().getRoleById(this.getRoleId()));
	}
	
	public void setRole(@NonNull final Role role){
		this.setRoleId(role.getIdLong());
	}
}
