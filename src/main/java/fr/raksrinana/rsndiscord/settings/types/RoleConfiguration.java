package fr.raksrinana.rsndiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.raksrinana.rsndiscord.Main;
import fr.raksrinana.rsndiscord.settings.IAtomicConfiguration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Role;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jetbrains.annotations.NotNull;
import java.util.Optional;
import static java.util.Optional.ofNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
public class RoleConfiguration implements IAtomicConfiguration{
	@JsonProperty("roleId")
	@Setter
	private long roleId;
	
	public RoleConfiguration(@NotNull Role role){
		this(role.getIdLong());
	}
	
	public RoleConfiguration(long roleId){
		this.roleId = roleId;
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(getRoleId()).toHashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof RoleConfiguration that)){
			return false;
		}
		return new EqualsBuilder().append(getRoleId(), that.getRoleId()).isEquals();
	}
	
	@Override
	public String toString(){
		return "Role(" + getRoleId() + ')';
	}
	
	@Override
	public boolean shouldBeRemoved(){
		return getRole().isEmpty();
	}
	
	@NotNull
	public Optional<Role> getRole(){
		return ofNullable(Main.getJda().getRoleById(getRoleId()));
	}
	
	public void setRole(@NotNull Role role){
		setRoleId(role.getIdLong());
	}
}
