package fr.mrcraftcod.gunterdiscord.settings.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.Main;
import net.dv8tion.jda.api.entities.Role;
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
public class RoleConfiguration{
	@JsonProperty("roleId")
	private long roleId;
	
	public RoleConfiguration(){
	}
	
	public RoleConfiguration(final Role role){
		this(role.getIdLong());
	}
	
	public RoleConfiguration(final long roleId){
		this.roleId = roleId;
	}
	
	@Override
	public String toString(){
		return this.getRole().map(Role::getAsMention).orElse("");
	}
	
	@Nonnull
	public Optional<Role> getRole(){
		return Optional.ofNullable(Main.getJDA().getRoleById(this.getRoleId()));
	}
	
	public void setRole(@Nonnull Role role){
		this.setRole(role.getIdLong());
	}
	
	private void setRole(long roleId){
		this.roleId = roleId;
	}
	
	public long getRoleId(){
		return this.roleId;
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
		if(!(o instanceof RoleConfiguration)){
			return false;
		}
		RoleConfiguration that = (RoleConfiguration) o;
		return new EqualsBuilder().append(getRoleId(), that.getRoleId()).isEquals();
	}
}
