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
	
	public WarnConfiguration(@Nonnull final Role role, final long delay){
		this.role = new RoleConfiguration(role);
		this.delay = delay;
	}
	
	@Override
	public int hashCode(){
		return new HashCodeBuilder(17, 37).append(this.getRole()).append(this.getDelay()).toHashCode();
	}
	
	@Override
	public boolean equals(final Object o){
		if(this == o){
			return true;
		}
		if(!(o instanceof WarnConfiguration)){
			return false;
		}
		final var that = (WarnConfiguration) o;
		return new EqualsBuilder().append(this.getDelay(), that.getDelay()).append(this.getRole(), that.getRole()).isEquals();
	}
	
	@Override
	public String toString(){
		return this.getRole().map(Role::getAsMention).orElse("<<EMPTY>>") + " for " + this.getDelay() + " seconds";
	}
	
	public Optional<Role> getRole(){
		return this.role.getRole();
	}
	
	public long getDelay(){
		return this.delay;
	}
	
	public void setDelay(final long delay){
		this.delay = delay;
	}
	
	public void setRole(@Nonnull final Role role){
		this.role = new RoleConfiguration(role);
	}
}
