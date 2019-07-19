package fr.mrcraftcod.gunterdiscord.settings.guild.warns;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.settings.types.RoleConfiguration;
import net.dv8tion.jda.api.entities.Role;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import javax.annotation.Nonnull;
import java.util.Optional;

public class WarnConfiguration{
	@JsonProperty("role")
	private RoleConfiguration role;
	@JsonProperty("delay")
	private long delay = 1L;
	
	public WarnConfiguration(){
	}
	
	public WarnConfiguration(@Nonnull Role role, long delay){
		this.role = new RoleConfiguration(role);
		this.delay = delay;
	}
	
	public long getDelay(){
		return delay;
	}
	
	public void setDelay(long delay){
		this.delay = delay;
	}
	
	@Override
	public String toString(){
		return getRole().map(Role::getAsMention).orElse("<<EMPTY>>") + " for " + this.getDelay() + " seconds";
	}
	
	public Optional<Role> getRole(){
		return role.getRole();
	}
	
	public void setRole(@Nonnull Role role){
		this.role = new RoleConfiguration(role);
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(getRole()).append(getDelay()).toHashCode();
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof WarnConfiguration)){
			return false;
		}
		WarnConfiguration that = (WarnConfiguration) o;
		return new EqualsBuilder().append(getDelay(), that.getDelay()).append(getRole(), that.getRole()).isEquals();
	}
}
