package fr.mrcraftcod.gunterdiscord.newSettings.guild.warns;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.mrcraftcod.gunterdiscord.newSettings.types.RoleConfiguration;
import net.dv8tion.jda.api.entities.Role;
import javax.annotation.Nonnull;
import java.util.Objects;
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
	
	public Optional<Role> getRole(){
		return role.getRole();
	}
	
	public long getDelay(){
		return delay;
	}
	
	@Override
	public boolean equals(Object obj){
		return obj instanceof WarnConfiguration && Objects.equals(this.role, ((WarnConfiguration) obj).role) && Objects.equals(this.getDelay(), ((WarnConfiguration) obj).getDelay());
	}
	
	@Override
	public String toString(){
		return getRole().map(Role::getAsMention).orElse("<<EMPTY>>") + " for " + this.getDelay() + " seconds";
	}
	
	public void setRole(@Nonnull Role role){
		this.role = new RoleConfiguration(role);
	}
	
	public void setDelay(long delay){
		this.delay = delay;
	}
}
